package scheduling;

import exceptions.NotSchedulableException;
import exceptions.NotDeschedulableException;
import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;
import view.VisualisationController;


import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static scheduling.Utilities.deepClone;

/**
 * Created by Ray on 28/07/2018.
 * Written by Kevin & Ray.
 * CLeaned by Olver and Dweeep
 */
public class DFBnBScheduler implements Scheduler{

	//variables for the gui
	private VisualisationController scheduleListener;

	//varibales for the graphs , nodes and edges
	private TaskGraph graph;
	protected int upperBound;
	protected int depth;
	protected int minDepth;
	protected long numPaths;
	protected long branchesPruned;

	// Index of the children of the schedule.
	private List<Integer> nodeIndices;
	private List<Integer> processorIndices;

	//variables for scheduling
	protected Schedule optimalSchedule;
	protected Schedule schedule;
	protected List<TaskNode> schedulableNodes;

	//the intial amount of nodes
	private List<TaskNode> initialNodes;

	//One of the Cost Function
	private Map<String, Integer> bottomLevelCosts = new HashMap<String ,Integer>();

	public DFBnBScheduler(TaskGraph graph, int processors) {

		this.graph = graph;

        //storing the initial nodes, indexs for the nodes and processors
        initialNodes = new ArrayList<>();

        //depth of all nodes in the nodeIndices array is set to Zero
        nodeIndices = new ArrayList<>(Collections.nCopies((graph.getNodes().size()), 0));
        processorIndices = new ArrayList<>(Collections.nCopies((graph.getNodes().size()), 0));
		schedule = new Schedule(processors, graph);
		schedulableNodes = schedule.getSchedulableNodes();

		//initialize depth, upperBound, and current time of the schedule
		minDepth = 0;
		depth = minDepth;
		numPaths = 1;
		branchesPruned = 0;

		// only initialise upper bound for non slave instances of the scheduler
		if (!(this instanceof DFBnBSlaveScheduler)) {

			//create a greedy schedule
            Schedule greedySchedule = new GreedyScheduler(graph, processors).createSchedule();

            //set the greedy schedule to be the optimal schedule as the deepclone
            optimalSchedule = (Schedule) deepClone(greedySchedule);

            //set the upperbound for the greedy schedule
            upperBound = greedySchedule.getBound();

            //while the there are still schedulable nodes iterate to the previous node
            while (greedySchedule.getScheduledNodes().size() > 0) {
                greedySchedule.removeLastScheduledTask();
            }
        }


        //CostFunction accounting for the bottom level costs
		for (TaskNode n : graph.getNodes()) {
		    Integer blp = criticalPath(n);
            bottomLevelCosts.put(n.getName(), blp);
        }

	}

	/**
	 * Returns the Optimal schedule according to the Depth First Search Branch and Bound
	 * @return Schedule object which is Optima
	 * @throws NotDeschedulableException Task not being able to be descheduled
	 * @throws NotSchedulableException Task not being able to be scheduled
	 */
	public Schedule createSchedule() throws NotDeschedulableException, NotSchedulableException {

		//visualisation updates for number of the schedules and number of paths
		updateSchedule();
		updateNumPaths();

		//variables & indexes for task and processors
		TaskNode nextTask;
		Processor nextProcessor;
		int nodeIndex;
		int processorIndex;

		//Variables to know when all initial nodes have been looped through
		boolean finished = false;

		//Used to not break on first iteration
		boolean initialIteration = true;

		//if there are still branches to be explored from the intial depth of 0 , keep looping through the loop
		while (depth >= minDepth) {

			//While there are still nodes to schedule
			while (schedulableNodes.size() > 0) {

				//check for whether the initial nodes have been repeated or not
				finished = removeReplicatedTree(initialIteration);

				//completes iteration through the initial nodes
				initialIteration = false;


				if (finished) {
					break;
				}


				//if the depth is less the than the amount of nodeIndices
				if (depth < nodeIndices.size()) {

					//get the index of the next node at the particular depth
					nodeIndex = nodeIndices.get(depth);

					//get the index of the the processor to schedule on
					processorIndex = processorIndices.get(depth);

					//otherwise intialize the node index and processor index to that of the deapth of 0
				} else {

					//changes the indexes to 0
					nodeIndex = 0;
					processorIndex = 0;

					//storing the node in nodeIndices
					nodeIndices.add(depth, nodeIndex);

					//storing the processor in processorIndices
					processorIndices.add(depth, processorIndex);


				}



				boolean skip = false;

				//if there are still any schedulable nodes
				if (nodeIndex < schedulableNodes.size()) {

					//get the next avialable node
					nextTask = schedulableNodes.get(nodeIndex);

					//get the processor to be required to schedule on
					nextProcessor = schedule.getProcessors().get(processorIndex);

					//removing the duplicates
					skip = removeDuplicates(nodeIndex, processorIndex, nextTask, nextProcessor);

					//checking if there is still a processor that we haven't tried to schedule this node on
				} else if (processorIndex < schedule.getProcessors().size() - 1){

					//rest the node index
					nodeIndices.set(depth, 0);

					//increment the processor index
					processorIndices.set(depth, processorIndices.get(depth) + 1);

					//get the depths of the node and processor
					nodeIndex = nodeIndices.get(depth);
					processorIndex = processorIndices.get(depth);

					//get the next available one
					nextTask = schedulableNodes.get(nodeIndex);

					//get the processor to schedule on
					nextProcessor = schedule.getProcessors().get(processorIndex);

					skip = removeDuplicates(nodeIndex, processorIndex, nextTask, nextProcessor);

				//otherwise there are no more nodes that can be scheduled at this depth
				} else {

					//reset hte node and processor index for that particular depth
					nodeIndices.set(depth, 0);
					processorIndices.set(depth, 0);

					//go to the previous depth
					depth--;

					//if depth becomes less than the minDepth value break this loop
					if (depth < minDepth) {
						break;
					}

					//if there are scheduled nodes
					if (schedule.getScheduledNodes().size() > 0) {

						//remove the last scheduled task from the most recent depth
						schedule.removeLastScheduledTask();

						//get the schedulable nodes
						schedulableNodes = schedule.getSchedulableNodes();
					}
					continue;
				}

				int est = schedule.getEarliestSchedulableTime(nextTask, nextProcessor);

				// if the Upperbound is greater it doesn't add the task
				if (est + nextTask.getWeight() < upperBound && !skip) {

					//adds the task and then iterates to the next depth
					schedule.addTask(nextTask, nextProcessor, est);
					schedulableNodes = schedule.getSchedulableNodes();
					nodeIndices.set(depth, nodeIndices.get(depth) + 1);
					depth++;

					//pruning visualisation update
				} else {

					//checks in advance for values which can be avoided
					nodeIndices.set(depth, nodeIndices.get(depth) + 1);
					branchesPruned++;

					//update the visualisation with the new numbers of branches pruned
					if (scheduleListener != null && (System.currentTimeMillis() % 100 == 0)) {
						updateBranchesPruned();
					}

				}
			}

			//ends the algorithim if the depth is lower than the MinDepth
			if (depth < minDepth) {
				break;
			}

			//iterate to the previous depth
			depth--;

			//if the algorithim is finished then break
			if (finished) {
				break;
			}

			//Updating the optimal schedule
			if (schedule.getBound() < upperBound || optimalSchedule == null) {

				//creating a clone for the optimal schedule
				optimalSchedule = (Schedule) deepClone(schedule);
				upperBound = schedule.getBound();

				//update visualisation with new optimal schedule
				updateSchedule();
				updateNumPaths();
			}

			//if there are any more schedulable nodes then you remove the last node and keep going up the tree till the
			//nodes that haven't been traversed have been traversed
			if (schedule.getScheduledNodes().size() > 0) {
				schedule.removeLastScheduledTask();
				numPaths++;

				//update the visualisation with the new number of paths
				if ((System.currentTimeMillis() % 100 == 0)) {
					updateNumPaths();
				}
			}

			//get the other schedulable nodes
			schedulableNodes = schedule.getSchedulableNodes();

		}

//		//if the optimal schedule is null then we print out out statements
//		if (optimalSchedule == null) {
//			System.out.println("No solution better than initial upper bound found on this branch.");
//		} else {
//			System.out.println("Solution with bound of " + optimalSchedule.getBound() + " found");
//		}

		//Updating the fields
		updateSchedule();
		updateNumPaths();
		updateBranchesPruned();
		finish();

		return optimalSchedule;

	}

	/**
	 * Calculates the cost function for a TaskNode on a Processor
	 * @param nextTask The taskNode we are looking at scheduling
	 * @param nextProcessor The processor we are looking at scheduling it on
	 * @return The 'cost' for scheduling that task on that processor
	 */
	private int costFunction(TaskNode nextTask, Processor nextProcessor) {

		//COST FUNCTION CODE---------------------------------------------

		// F-IDLE CALCULATION -------------------------------------------

		//obtains the total cost of the nodes
		int totalCostOfNodes = 0;
		for (TaskNode node : graph.getNodes()) {
			totalCostOfNodes = totalCostOfNodes + node.getWeight();
		}

		int totalIdleTime = 0;

		// obtains the idle time associated with a particular partial schedule
		for (Processor p : schedule.getProcessors()) {

			//use the entire bound of the processor
			int totalTime = schedule.getBound();

			int totalTaskWeight = 0;

			//storing the total task weight
			for (TaskNode tasks : p.getTasks()) {
				totalTaskWeight = totalTaskWeight + tasks.getWeight();
			}

			//obtaining the total idle time
			totalIdleTime = totalIdleTime + totalTime - totalTaskWeight;
		}

		// fIdle formula calculation
		int fIdle = (totalCostOfNodes + totalIdleTime) / schedule.getProcessors().size();

		// F-BOTTOM-LEVEL CALCULATION ----------------------------------------------

		// fbl(s) = max(start time + bottom level weight) of all nodes of that partial schedule.

		int fblTemp;

		//the current maximum fbl intialises
		int fblMax = 0;

		// Loop through all the scheduled nodes, then the node we want to schedule and find the maxFbl out of them.
		for (TaskNode tn : schedule.getScheduledNodes()) {

			//obtain the bottom level costs
			Integer value = bottomLevelCosts.get(tn.getName());
			fblTemp = tn.getStartTime() + value;

			//update teh maximum fbl value
			fblMax = Math.max(fblMax, fblTemp);
		}

		// Get startime cannot be called on the child node since that node hasn't been scheduled yet
		int childStartTime = schedule.getEarliestSchedulableTime(nextTask, nextProcessor);

		Integer value = bottomLevelCosts.get(nextTask.getName());
		fblTemp = childStartTime + value;

		//update the fblmax value
		fblMax = Math.max(fblMax, fblTemp);

		// fDRT CALCULATION -------------------------------------------------------------------------
		// fDRT(s) = max{tdr(n) + blw(n) for every free node.
		// tdr(n) = min (tdr(n,P) i.e. earliest schedulable node.
		// tdr(n,P) is earliest schedulable time.

//		int fDRT = schedule.getEarliestSchedulableTime(nextTask,nextProcessor) + childStartTime;

//		child.setCostFunction(Math.max(fblMax, fIdle));

//		return Math.max(Math.max(fblMax, fIdle), fDRT);
//		System.out.println("Cost function is: " + Math.max(fblMax,fIdle));
		return Math.max(fblMax,fIdle);
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

		//Loops through all the children nodes based on the outgoing edges
		for (TaskEdge edge : node.getOutgoingEdges()) {
			TaskNode child = edge.getEndNode();

			//Recursion is used to obtain the bottom level path
			int temp = dfsCriticalPath(child) + child.getWeight();

			//the maximum weight is selected
			if (temp > ans) {
				ans = temp;
			}
		}

		return ans;
	}




	/**
	 * This method removes replicated parts of the tree depending on the number of initial nodes
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

				//If all initial nodes have been looped through, we are finished
				if (index == schedulableNodes.size()) {
					finished = true;
					break;
				}

				TaskNode node = schedulableNodes.get(index);

				//If initialNode not been seen, add it to list
				// Then break to look through all of its children 
				if (!initialNodes.contains(node)) {
					initialNodes.add(node);
					addedNode = true;
				}
				index++;
			}

			//If all initial nodes have been seen, set finished to true to finish the algorithm
			//As the optimal solution has been found
			if (initialNodes.equals(schedulableNodes)) {
				finished = true;
			}

		}
		return finished;

	}



	/**
	 * This method skips duplicate nodes from the tree
	 * @param nodeIndex The index of the nextTask
	 * @param processorIndex The index of the nextProcessor
	 * @param nextTask The potential next task
	 * @param nextProcessor The processor the task would be scheduled on
	 * @return boolean whether to skip the current node
	 */
	private boolean removeDuplicates(int nodeIndex, int processorIndex, TaskNode nextTask, Processor nextProcessor) {

		//This code block used to remove duplicate entities in search tree
		boolean parentsOnSameProcessor = true;

		//Loop through parents of current node
		for (TaskEdge edge : nextTask.getIncomingEdges()) {
			TaskNode parent = edge.getStartNode();

			//Statement to determine if current processor has no parent nodes of current node
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

	/**
	 * Sets the scheduleListener with that of viusalisation controller
	 * @param listener , that listens to the action events
	 */
	public void setScheduleListener(VisualisationController listener) {
		this.scheduleListener = listener;
	}

	/**
	 * Notifies listener of new optimal schedule.
	 */
	public void updateSchedule() {

		// checking to see if the schedulelistener object has been created
		if (scheduleListener != null) {

			//updates the listener based on the optimal schedule
			scheduleListener.updateSchedule(optimalSchedule);

			//make the listener sleep for 200msecs
			try {
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Updates listener with number of branches pruned.
	 */
	public void updateBranchesPruned() {

		// checking to see if the schedulelistener object has been created
		if (scheduleListener != null) {

			//update the number of pruned branches
			scheduleListener.updateBranchesPruned(branchesPruned);       }

	}

	/**
	 * Updates listener with number of paths searched.
	 */
	public void updateNumPaths() {

		//checking to see if the schedulelistener object has been created
		if (scheduleListener != null) {

			//update the number of paths
			scheduleListener.updateNumPaths(numPaths);
		}
	}

	/**
	 * Updates listener that algorithm has finished.
	 */
	public void finish() {

		//checking if the schedulableListener object has been created
		if (scheduleListener != null) {

			//updates the listener that the algorithim has finished
			scheduleListener.finish();
		}
	}



}
