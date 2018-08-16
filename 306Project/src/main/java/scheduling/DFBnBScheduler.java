package scheduling;

import exceptions.NotSchedulableException;
import exceptions.NotDeschedulableException;
import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;
import view.RootLayout;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ray on 28/07/2018.
 * Written by Kevin & Ray.
 */
public class DFBnBScheduler implements Scheduler{

	private RootLayout scheduleListener;

	private TaskGraph graph;
	private int upperBound;
	private int depth;
	private int numPaths;

	// Index of the children of the schedule.
	private List<Integer> nodeIndices;
	private List<Integer> processorIndices;

	private Schedule optimalSchedule;
	private Schedule schedule;
	private List<TaskNode> schedulableNodes;

	private List<TaskNode> initialNodes;

	public DFBnBScheduler(TaskGraph graph, int processors) {
		this.graph = graph;

		initialNodes = new ArrayList<TaskNode>();
		nodeIndices = new ArrayList<Integer>();
		processorIndices = new ArrayList<Integer>();
		schedule = new Schedule(processors, graph);
		schedulableNodes = schedule.getSchedulableNodes();

		//initialize depth, upperBound, and current time of the schedule
		depth = 0;
		numPaths = 0;

		Schedule greedySchedule = new GreedyScheduler(graph, processors).createSchedule();

		upperBound = greedySchedule.getBound();

		while (greedySchedule.getScheduledNodes().size() > 0) {
			greedySchedule.removeLastScheduledTask();
		}

	}


	public Schedule createSchedule() throws NotDeschedulableException, NotSchedulableException {
		//initialize upperBound

		TaskNode nextTask = null;
		Processor nextProcessor = null;
		int nodeIndex = 0;
		int processorIndex = 0;

		//Variables to know when all initial nodes have been looped through
		boolean finished = false;

		//Used to not break on first iteration
		boolean initialIteration = true;

		//while there are branches to explore from depth 0, keep looping through all branches
		while (depth >= 0) {

			while (schedulableNodes.size() > 0) { //while there are still nodes to schedule
				// System.out.println("Searching at depth " + depth + " with bound " + schedule.getBound());

				//Determine wheather initial nodes have been repeated
				finished = removeReplicatedTree(initialIteration);

				initialIteration = false;

				if (finished) {
					break;
				}


				if (depth < nodeIndices.size()) {
					nodeIndex = nodeIndices.get(depth); //get the index of the next node at that depth
					processorIndex = processorIndices.get(depth); //get the index of the processor to schedule on
				} else { //otherwise initialise the node index and processor index of that depth as 0
					nodeIndex = 0;
					processorIndex = 0;
					nodeIndices.add(depth, nodeIndex);

					processorIndices.add(depth, processorIndex);


				}



				boolean skip = false;
				if (nodeIndex < schedulableNodes.size()) { //if there is still schedulable nodes
					nextTask = schedulableNodes.get(nodeIndex); //get the next available one
					nextProcessor = schedule.getProcessors().get(processorIndex); //get the processor to schedule on

					skip = removeDuplicates(nodeIndex, processorIndex, nextTask, nextProcessor);

				} else if (processorIndex < schedule.getProcessors().size() - 1){ //if there is still a processor that we haven't tried to schedule this node on
					nodeIndices.set(depth, 0); //reset the node index
					processorIndices.set(depth, processorIndices.get(depth) + 1); //increment the processor index

					nodeIndex = nodeIndices.get(depth);
					processorIndex = processorIndices.get(depth);

					nextTask = schedulableNodes.get(nodeIndex); //get the next available one
					nextProcessor = schedule.getProcessors().get(processorIndex); //get the processor to schedule on

					skip = removeDuplicates(nodeIndex, processorIndex, nextTask, nextProcessor);


				} else { //otherwise no more nodes can be scheduled at this depth
					nodeIndices.set(depth, 0); //reset node index for that depth
					processorIndices.set(depth, 0);

					depth--; //go to previous depth

					if (depth < 0) {
						break;
					}

					//if there are scheduled nodes
					if (schedule.getScheduledNodes().size() > 0) {
						schedule.removeLastScheduledTask(); //remove the last scheduled task from the most recent depth

						schedulableNodes = schedule.getSchedulableNodes(); //get schedulable nodes
					}
					continue;
				}

				schedule.addTask(nextTask, nextProcessor, schedule.getEarliestSchedulableTime(nextTask, nextProcessor));

//				costFunction(nextTask);

				// Run the cost function for each of the tasks children to determine which one to schedule first.
				TaskNode minTask;			// The node and processor that has the lowest cost
				Processor minProcessor;

				int minCost = Integer.MAX_VALUE;
				int minCostTemp;
				for (TaskEdge e: nextTask.getOutgoingEdges()){
					TaskNode tn = e.getEndNode();
					for (Processor p: schedule.getProcessors()) {
						minCostTemp = costFunction(tn, p);
						if (minCostTemp<minCost) {
							minTask = tn;
							minProcessor = p;
						}
					}
				}
				// TODO: Actually make use of minTask and minProcessor

				schedulableNodes = schedule.getSchedulableNodes();
				nodeIndices.set(depth, nodeIndices.get(depth) + 1);

				// kind of pruning
				if ((schedule.getBound() > upperBound) || (skip == true)){
					schedule.removeLastScheduledTask();
					schedulableNodes = schedule.getSchedulableNodes();

					depth--;

					if (depth < 0) {
						break;
					}

				}

				depth++;
			}

			if (depth < 0) {
				break;
			}

			depth--; //go to previous depth

			if (finished) {
				break;
			}
			//TODO clone schedule and set optimal schedule to be this schedule
			if (schedule.getBound() < upperBound || optimalSchedule == null) {
				optimalSchedule = (Schedule) deepClone(schedule);
				upperBound = schedule.getBound();
				if (scheduleListener != null) { //update visualisation with new optimal schedule
					updateGUISchedule(false);
				}
			}

			if (schedule.getScheduledNodes().size() > 0) {
				schedule.removeLastScheduledTask();
				numPaths++;
				if (scheduleListener != null) { //update visualisation with new number of paths
					scheduleListener.updateNumPaths(numPaths);
				}
			}

			schedulableNodes = schedule.getSchedulableNodes();



			//                nodeIndices.set(depth, nodeIndices.get(depth) + 1);


		}

		System.out.println("Solution with bound of " + optimalSchedule.getBound() + " found");
		if (scheduleListener != null) {
			updateGUISchedule(true);;
		}
		return optimalSchedule;

	}

	/**
	 * This method makes a "deep clone" of any object it is given.
	 */
	public static Object deepClone(Object object) {
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
	 * Calculates the cost function for a TaskNode on a Processor
	 * @param nextTask The tasknode we are looking at scheduling
	 * @param nextProcessor The processor we are looking at scheduling it on
	 * @return The 'cost' for scheduling that task on that processor
	 */
	private int costFunction(TaskNode nextTask, Processor nextProcessor) {

		//COST FUNCTION CODE---------------------------------------------

		// F-IDLE CALCULATION -------------------------------------------

		// Gets the total cost of all nodes
		int totalCostOfNodes = 0;
		for (TaskNode node : graph.getNodes()) {
			totalCostOfNodes = totalCostOfNodes + node.getWeight();
		}

		int totalIdleTime = 0;
		// Gets the total idle time for that partial schedule
		for (Processor p : schedule.getProcessors()) {
			int totalTime = schedule.getBound(); // Use the entire bound of the processor
			int totalTaskWeight = 0;
			for (TaskNode tasks : p.getTasks()) {
				totalTaskWeight = totalTaskWeight + tasks.getWeight();
			}
			totalIdleTime = totalIdleTime + totalTime - totalTaskWeight;
		}

		// fIdle formula calculation
		int fIdle = (totalCostOfNodes + totalIdleTime) / schedule.getProcessors().size();

		// F-BOTTOM-LEVEL CALCULATION ----------------------------------------------
		// fbl(s) = max(start time + bottom level weight) of all nodes of that partial schedule.

		int fblTemp;
		int fblMax = 0;    // The currently highest maximum fbl.

		// Loop through all the scheduled nodes, then the node we want to schedule and find the maxFbl out of them.
		for (TaskNode tn : schedule.getScheduledNodes()) {
			fblTemp = tn.getStartTime() + criticalPath(tn);
			fblMax = Math.max(fblMax, fblTemp);
		}

		// Can't call getStartTime on the child node because we haven't scheduled it yet.
		int childStartTime = 0;
		childStartTime = Math.max(schedule.getEarliestSchedulableTime(nextTask, nextProcessor), childStartTime);
		fblTemp = childStartTime + criticalPath(nextTask);
		fblMax = Math.max(fblMax, fblTemp);

		// TODO: fDRT calculation


//		int heuristic = Math.max(fblMax, fIdle);
//		child.setCostFunction(heuristic);

		// TODO: Double check if this should be max or min.
		return Math.max(fblMax, fIdle);
	}

	/**
	 * Calculates the longest path from the current node to an end node.
	 * @param node The node we're finding the critical path for.
	 * @return int: The highest critical path for that node.
	 */
	public int criticalPath(TaskNode node) {
		return dfsCriticalPath(node) + node.getWeight();
	}
	
	/**
	 * This is used to find the highest critical path Does the DFS over the tree.
	 * @param node The node we're finding the critical path for.
	 * @return int: The highest critical path for that node's children.
	 */
	private int dfsCriticalPath(TaskNode node) {
		
		int ans = 0;
		
		//Loop through all children
		for (TaskEdge edge : node.getOutgoingEdges()) {
			TaskNode child = edge.getEndNode();
			
			//Do recursion as described by oliver's task scheduling for bottom level path
			int temp = dfsCriticalPath(child) + child.getWeight();
			
			//Select the maximum weight of the current path
			if (temp > ans) {
				ans = temp;
			}
		}
		
		return ans;
	}




	/**
	 * This method removes replciated parts of the tree depending on the number of initial nodes
	 * @param initialIteration Is this the first iteration?
	 * @return boolean determining weather to finish the algorithm
	 */
	private boolean removeReplicatedTree(boolean initialIteration) {

		boolean finished = false;

		//Block for determining if initial nodes have been seen
		if ((depth == 0) && (initialIteration == false) ) {

			boolean addedNode = false;
			int index = 0;
			//Loop through initial nodes
			while (addedNode == false) {
				TaskNode node = schedulableNodes.get(index);
				//If initalNode not been seen, add it to list 
				// Then break to look through all of its children 
				if (!initialNodes.contains(node)) {
					initialNodes.add(node);
					addedNode = true;
				}
				index++;
			}
			//If all initial nodes have been seen, set finished to true to finish the algorithum
			//As the optimal solution has been found
			if (initialNodes.equals(schedulableNodes)) {
				finished = true;
			}

		}
		return finished;

	}



	/**
	 * This method skips duplicate nodes from the tree
	 * @param nodeIndex
	 * @param processorIndex
	 * @param nextTask
	 * @param nextProcessor
	 * @return boolean wheather to skip the current node
	 */
	private boolean removeDuplicates(int nodeIndex, int processorIndex, TaskNode nextTask, Processor nextProcessor) {

		//This code block used to remove duplicate entities in search tree
		boolean parentsOnSameProcessor = true;
		//Loop through parents of current node
		for (TaskEdge edge : nextTask.getIncomingEdges()) {
			TaskNode parent = edge.getStartNode();

			//Statment to determine if current processor has no parent nodes of current node
			if (!parent.getProcessor().equals(nextProcessor)) {
				parentsOnSameProcessor = false;
			}
			else {
				parentsOnSameProcessor = true;
				break;
			}
		}


		boolean duplicate = false;
		for (Processor p : schedule.getProcessors()) {
			//If current processor different to next one which next node will be scheduled on
			if (!p.equals(nextProcessor)) {

				List<TaskNode> tasks = p.getTasks();

				if (tasks.size() > 0) {
					//Get the latest task from this processor
					TaskNode latestTask = tasks.get(tasks.size() - 1);

					//Loop through parent nodes of this task
					for (TaskEdge edge : nextTask.getIncomingEdges()) {
						TaskNode parent = edge.getStartNode();    
						//If the node scheduled on this processor was the parent of the the 
						//Current node, means it is a duplicate situation
						if (latestTask.equals(parent)) {
							duplicate = true;
							break;
						}
					}
				}
			}
		}

		//If node processor has no other parents and is a duplicate return true to skip else return false
		if ( (parentsOnSameProcessor == false) && (duplicate == true) ){
			return true;
		}
		else {
			return false;
		}

	}

	public void setScheduleListener(RootLayout listener) {
		this.scheduleListener = listener;
	}

	private void updateGUISchedule (boolean done) {
		scheduleListener.updateSchedule(optimalSchedule, done);
		try {
			TimeUnit.MILLISECONDS.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
