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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ray on 28/07/2018.
 * Written by Kevin & Ray.
 */
public class DFBnBMasterScheduler {

	private VisualisationController scheduleListener;

	private List<Schedule> partialSchedules = new ArrayList<>();
	private int numCores;

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

	private Map<String, Integer> bottomLevelCosts = new HashMap<String ,Integer>();

	public DFBnBMasterScheduler(TaskGraph graph, int processors, int numCores) {
		upperBound = Integer.MAX_VALUE;
	}

	/**
	 * Runs a bfs to allocate a thread for each schedule
	 */
	private void runBFS() {
		if (numCores<= schedule.getSchedulableNodes().size()) {
			// Assign
		} else {
			int numOfChildren=0;
			int depthBFS=0;
			int nodeIndex;
			nodeIndices = new ArrayList<>();



//			List<TaskNode> parentNodes = schedule.getSchedulableNodes();
//
//			for (TaskNode tn: parentNodes) {
//				schedule.addTask(tn, schedule.getProcessors().get(0), schedule.getEarliestSchedulableTime(tn,schedule.getProcessors().get(0)));
//				numOfChildren += schedule.getSchedulableNodes().size() * schedule.getProcessors().size();
//
//				nodeIndex = nodeIndices.get(depthBFS);
//				nodeIndices.add(depthBFS, nodeIndex);
//
//				nodeIndices.set(depthBFS, nodeIndices.get(depthBFS) + 1 );
//
//				schedule.removeLastScheduledTask();
//
//
//			}
//


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


	public void initialisePartialSchedules() {

		int scheduleIndex = 0;

		while (partialSchedules.size() < numCores) {

			createPartialSchedules(schedule);
			schedule = partialSchedules.get(scheduleIndex);
			scheduleIndex++;

		}

	}



	private void createPartialSchedules(Schedule s) {
		for (TaskNode node : s.getSchedulableNodes()) {
			for(Processor p: s.getProcessors()) {
				Schedule partialSchedule = (Schedule) deepClone(s);
				partialSchedule.addTask(node, p, partialSchedule.getEarliestSchedulableTime(node, p));
				partialSchedules.add(partialSchedule);
			}
		}

		if (partialSchedules.contains(s)) {
			partialSchedules.remove(s);
		}


	}

}
