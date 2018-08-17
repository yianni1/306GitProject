package scheduling.parallel;

import exceptions.NotSchedulableException;
import exceptions.NotDeschedulableException;
import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;
import scheduling.GreedyScheduler;
import scheduling.Processor;
import scheduling.Schedule;
import scheduling.Scheduler;
import view.VisualisationController;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ray on 28/07/2018.
 * Written by Kevin & Ray.
 */
public class DFBnBMasterScheduler {

	private VisualisationController scheduleListener;

	private List<Schedule> partialSchedules = new ArrayList<>();
	private int numCores;
	private int processors;

	private TaskGraph graph;
	private int upperBound;
	private int depth;
	private long numPaths;
	private long branchesPruned;

	// Index of the children of the schedule.
	private List<Integer> nodeIndices;
	private List<Integer> processorIndices;

	private Schedule optimalSchedule;
	private Schedule schedule;
	private List<TaskNode> schedulableNodes;

	private List<TaskNode> initialNodes;

	public DFBnBMasterScheduler(TaskGraph graph, int processors, int numCores) {
		this.graph = graph;
		this.processors = processors;
		this.numCores = numCores;
		schedule = new Schedule(processors, graph);
	}

	public void createSchedule() {
		Schedule greedySchedule = new GreedyScheduler(graph, processors).createSchedule();

		upperBound = greedySchedule.getBound();

		while (greedySchedule.getScheduledNodes().size() > 0) {
			greedySchedule.removeLastScheduledTask();
		}

		initialisePartialSchedules();

		//TODO set each slave to run on a separate thread
		for (Schedule schedule : partialSchedules) {
			DFBnBSlaveScheduler scheduler = new DFBnBSlaveScheduler(schedule.getGraph(), processors, schedule, upperBound);
			scheduler.createSchedule();
		}
	}

	public List<Schedule> getPartialSchedules() {
		return partialSchedules;
	}




	public void initialisePartialSchedules() {

		int scheduleIndex = 0;

		while (partialSchedules.size() < numCores) {
			for (int i = 0; i < schedule.getProcessors().size(); i++) {
				try {
					createPartialSchedules(schedule, i);
				}
				catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
			schedule = partialSchedules.get(scheduleIndex);
			scheduleIndex++;

		}

	}



	private void createPartialSchedules(Schedule s, int processorId) throws CloneNotSupportedException {
		List<TaskNode> nodes = s.getSchedulableNodes();
		for (int i = 0; i < s.getSchedulableNodes().size(); i++) {
			s.addTask(nodes.get(i), s.getProcessors().get(processorId), s.getEarliestSchedulableTime(nodes.get(i), s.getProcessors().get(processorId)));
			Schedule partialSchedule = (Schedule) deepClone(s);
			s.removeLastScheduledTask();
			partialSchedules.add(partialSchedule);
		}

		if (partialSchedules.contains(s)) {
			partialSchedules.remove(s);
		}


	}

	/**
	 * This method makes a "deep clone" of any object it is given.
	 */
	private static Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
