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

	//variables around visualization
	private VisualisationController scheduleListener;

	//vars around the algorithim
	private List<Schedule> partialSchedules = new ArrayList<>();
	private int numCores;

	private TaskGraph graph;
	private int upperBound;
	private int depth;
	private long numPaths;
	private long branchesPruned;


	//Indexing for the children of the schedule
	private List<Integer> nodeIndices;
	private List<Integer> processorIndices;

	//vars for traversal and storing schedules
	private Schedule optimalSchedule;
	private Schedule schedule;
	private List<TaskNode> schedulableNodes;

	private List<TaskNode> initialNodes;

	/**
	 * Constructor for the Master scheduler sets upperbound limit, and creates new schedule based on the graph
	 * and the processor number
	 * @param graph
	 * @param processors
	 * @param numCores
	 */
	public DFBnBMasterScheduler(TaskGraph graph, int processors, int numCores) {
		upperBound = Integer.MAX_VALUE;
		this.numCores = numCores;
		schedule = new Schedule(processors, graph);
	}

	/**
	 * This method runs the Breadth first search iteratively on corresponding layer after the initai layer to assign
	 * threads to each of the schedules
	 */
	private void runBFS() {

		//if the amount of cores is less then the amount of schedulable nodes
		if (numCores <= schedule.getSchedulableNodes().size()) {

			// Assign A thread to that particular node

		} else {

			//resetting values??
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
	 * Gets a list of partial schedules
	 * @return partialSchedules
	 */
	public List<Schedule> getPartialSchedules() {
		return partialSchedules;
	}

	/**
	 * This method makes a Deepclone of the object that is parsed into it
	 * @param object
	 * @return ois.readObject() //clone of the object
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

	/**
	 * This method initializes the partial schedule which creates partial schedules if the
	 * amount of core is less
	 */
	public void initialisePartialSchedules() {


		int scheduleIndex = 0;

		//if the number of partial schedules is less than that of the number of cores
		while (partialSchedules.size() < numCores) {

			try {
				//creates a partial schedule
				createPartialSchedules(schedule);
			}
			catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}

			//gets the current index of the partial schedules and then iterates to the next index
			schedule = partialSchedules.get(scheduleIndex);
			scheduleIndex++;

		}

	}


	/**
	 * This method creates partial schedules through deepclonning schedules which can then be checked for task addition
	 * @param s
	 * @throws CloneNotSupportedException
	 */
	private void createPartialSchedules(Schedule s) throws CloneNotSupportedException {

		//iterating through the schedulable nodes
		for (TaskNode node : s.getSchedulableNodes()) {

			//iterating through processors
			for(Processor p: s.getProcessors()) {

				//creating a deepclone of schedule parameter
				Schedule partialSchedule = (Schedule) deepClone(s);

				//TODO make sure that nodes being scheduled on multiple processors will already be scheduled on second iteration

				//the earliest schedulable time will be -1 when getting the value from this loop and this will not cause an scheduled exception
				int time = partialSchedule.getEarliestSchedulableTime(node, p);

				//add the task to the partial schedule and then to the list of partial schedules
				partialSchedule.addTask(node, p, time);
				partialSchedules.add(partialSchedule);
			}

		}

		//if the partial contains s then it removes that s from the partial schedule list
		if (partialSchedules.contains(s)) {
			partialSchedules.remove(s);
		}


	}

}
