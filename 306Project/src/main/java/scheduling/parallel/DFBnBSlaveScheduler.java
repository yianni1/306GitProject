package scheduling.parallel;

import exceptions.NotDeschedulableException;
import exceptions.NotSchedulableException;
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
import java.util.concurrent.TimeUnit;

/**
 * Created by Ray on 28/07/2018.
 * Written by Kevin & Ray.
 */
public class DFBnBSlaveScheduler implements Scheduler{

	private VisualisationController scheduleListener;

	// variables associated with the graph
	private TaskGraph graph;
	private int upperBound;
	private int depth;
	private int minDepth;
	private long numPaths;
	private long branchesPruned;
	private List<TaskNode> initialNodes;

	// Index of the children of the schedule.
	private List<Integer> nodeIndices;
	private List<Integer> processorIndices;

	//variables assoicated with the schedules
	private Schedule optimalSchedule;
	private Schedule schedule;
	private List<TaskNode> schedulableNodes;

	//vars associated with pruning
	private Map<String, Integer> bottomLevelCosts = new HashMap<String ,Integer>();

	/**
	 * The Constructor associated with the Slave Schedular which conducts DFS algorithim for particular branches on seperate threads
	 * @param graph
	 * @param processors
	 * @param initialSchedule
	 * @param initialUpperBound
	 */
	public DFBnBSlaveScheduler(TaskGraph graph, int processors, Schedule initialSchedule, int initialUpperBound) {

		this.graph = graph;

		//storing the initial nodes, indexs for the nodes and processors
		initialNodes = new ArrayList<>();
		nodeIndices = new ArrayList<>(Collections.nCopies((graph.getNodes().size()), 0)); //depth of all nodes in the nodeIndices array is set to zero
		processorIndices = new ArrayList<>(Collections.nCopies((graph.getNodes().size()), 0));

		//initializing the initial blank schedule and getting the amount schedulable nodes within the graph
		schedule = initialSchedule;
		schedulableNodes = schedule.getSchedulableNodes();

		//initialize depth, upperBound, and current time of the schedule
		depth = schedule.getScheduledNodes().size();
		minDepth = depth;
		numPaths = 1;

		upperBound = initialUpperBound;


		for (TaskNode n : graph.getNodes()) {
		    Integer blp = criticalPath(n);
            bottomLevelCosts.put(n.getName(), blp);
        }

	}

	/**
	 * Returns an schedule which is found upon the termination of the Depth first search branch and bound algorithim
	 * @return Schedule object which is a valid solution
	 * @throws NotDeschedulableException Task cannot be descheduled
	 * @throws NotSchedulableException Task cannot be scheduled
	 */
	public Schedule createSchedule() throws NotDeschedulableException, NotSchedulableException {

		//updating the inital schedule so that it is visible in the GUI
		if (scheduleListener != null) {
			updateGUISchedule();
			scheduleListener.updateNumPaths(numPaths);
		}


		TaskNode nextTask;
		Processor nextProcessor;
		int nodeIndex;
		int processorIndex;

		//Variables to know when all initial nodes have been looped through
		boolean finished = false;

		//Used to not break on first iteration
		boolean initialIteration = true;

		//while there are branches to explore from depth 0, keep looping through all branches
		while (depth >= minDepth) {

			//checking if there are still any nodes that are schedulable
			while (schedulableNodes.size() > 0) {

				//checking if the inital node have been repeated
				finished = removeReplicatedTree(initialIteration);


                if (initialIteration) {

                	//intializing the cost function
                    int costF;
                    for (TaskNode tn : schedulableNodes) {
                        costF = Integer.MAX_VALUE;

                        //resetting cost function if the value is lower than the preivous value
                        for (Processor p: schedule.getProcessors()) {
                            //costF = costFunction(tn, p);
                            int currentCF = costFunction(tn,p);
                            if (currentCF <= costF) {
                                tn.setCostFunction(currentCF);
                            }

                        }
                    }
                }

				//iterating the through inital nodes completed
				initialIteration = false;

				if (finished) {
					break;
				}

				//get the index of the node at that particular depth
				nodeIndex = nodeIndices.get(depth);

				//get the index of the processor to schedule on
				processorIndex = processorIndices.get(depth);


				boolean skip = false;

				//checking if there are any more schedulable nodes
				if (nodeIndex < schedulableNodes.size()) {

					//obtain the next available schedulable node
					nextTask = schedulableNodes.get(nodeIndex);

					//obtain the processor to schedule the particular node on
					nextProcessor = schedule.getProcessors().get(processorIndex);

					//remove duplicate schedules in the tree, (the same schedule just started on a different processor)
					skip = removeDuplicates(nodeIndex, processorIndex, nextTask, nextProcessor);

					//iterating through all the processors and understanding whether we have tried to schedule this node on
				} else if (processorIndex < schedule.getProcessors().size() - 1){

					//reset the node index
					nodeIndices.set(depth, 0);

					//move on to the next processor
					processorIndices.set(depth, processorIndices.get(depth) + 1);

					//get the depth of the current node and the processor
					nodeIndex = nodeIndices.get(depth);
					processorIndex = processorIndices.get(depth);

					//get the next available node
					nextTask = schedulableNodes.get(nodeIndex);

					//obtain a processor to schedule on
					nextProcessor = schedule.getProcessors().get(processorIndex);

					//remove any duplicate schedules
					skip = removeDuplicates(nodeIndex, processorIndex, nextTask, nextProcessor);


				} else { //otherwise no more nodes can be scheduled at this depth

					//reset the node and processor index for the graph
					nodeIndices.set(depth, 0);
					processorIndices.set(depth, 0);

					//iterate back to the previous depth for the graph
					depth--;

					//depth greater than the mindepth then break
					if (depth < minDepth) {
						break;
					}

					//if there are scheduled nodes, remove the last schedulable node and get the schedulable nodes
					if (schedule.getScheduledNodes().size() > 0) {
						schedule.removeLastScheduledTask();

						schedulableNodes = schedule.getSchedulableNodes();
					}
					continue;
				}

				int est = schedule.getEarliestSchedulableTime(nextTask, nextProcessor);

				//  Checking if the weight is lower than the upperbound in that case it would add a task
				if (est + nextTask.getWeight() < upperBound && !skip) {
					//System.out.println("Task " + nextTask.getName() + " on schedule "+nextProcessor.getID()+" will be less than the upper bound. ("+est+" vs. "+ upperBound+")");
					schedule.addTask(nextTask, nextProcessor, est);

//				// Run the cost function for each of the tasks children to determine which one to schedule first.
//				TaskNode minTask;			// The node and processor that has the lowest cost
//				Processor minProcessor;

//                int costF;
//				for (TaskEdge e: nextTask.getOutgoingEdges()){
//					TaskNode tn = e.getEndNode();
//                    costF = Integer.MAX_VALUE;
//
//                    for (Processor p: schedule.getProcessors()) {
//
//					    //costF = costFunction(tn, p);
//                        int currentCF = costFunction(tn,p);
//
//					    if (currentCF <= costF) {
//                            tn.setCostFunction(currentCF);
//                        }
//					}
//				}

					//get the schedulable nodes , iterate to the next depth and increase depth
					schedulableNodes = schedule.getSchedulableNodes();
					nodeIndices.set(depth, nodeIndices.get(depth) + 1);
					depth++;



				} else {	//pruning visualisation update and pruning call
					nodeIndices.set(depth, nodeIndices.get(depth) + 1);
					branchesPruned++;
					if (scheduleListener != null && (System.currentTimeMillis() % 100 == 0)) { //update visualisation with new number of branches pruned
						scheduleListener.updateBranchesPruned(branchesPruned);
					}

				}
			}

			if (depth < minDepth) {
				break;
			}

			depth--; //go to previous depth

			if (finished) {
				break;
			}

			//if the schedule bound is lower than the upperbound and no optimal schedule has been stored then
			//it stores the current schedule by retrieving a deep clone
			if (schedule.getBound() < upperBound || optimalSchedule == null) {
				optimalSchedule = (Schedule) deepClone(schedule);
				upperBound = schedule.getBound();

				//updating optimal schedule to be shown in the visualisation
				if (scheduleListener != null) {
					updateGUISchedule();
					scheduleListener.updateNumPaths(numPaths);
				}
			}


			if (schedule.getScheduledNodes().size() > 0) {
				schedule.removeLastScheduledTask();
				numPaths++;
				if (scheduleListener != null && (System.currentTimeMillis() % 100 == 0)) { //update visualisation with new number of paths
					scheduleListener.updateNumPaths(numPaths);
				}
			}



			schedulableNodes = schedule.getSchedulableNodes();


		}

		//robustness handling
		if (optimalSchedule != null) {
			System.out.println("Solution with bound of " + optimalSchedule.getBound() + " found");
		} else {
			System.out.println("No solution better than upper bound found on this branch.");
		}

		if (scheduleListener != null) {
            updateGUISchedule();;
			scheduleListener.updateNumPaths(numPaths);
			scheduleListener.updateBranchesPruned(branchesPruned);
			scheduleListener.finish();
        }
		return optimalSchedule;

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

	/**
	 * This method calculates the cost function based on the task and processor
	 * @param nextTask The Tasknode which is going to be scheduled
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

			//Use the entire bound of the processor
			int totalTime = schedule.getBound();
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
            Integer value = bottomLevelCosts.get(nextTask.getName());
			fblTemp = tn.getStartTime() + value;
			fblMax = Math.max(fblMax, fblTemp);
		}

		// Can't call getStartTime on the child node because we haven't scheduled it yet.
		int childStartTime = schedule.getEarliestSchedulableTime(nextTask, nextProcessor);
        Integer value = bottomLevelCosts.get(nextTask.getName());
		fblTemp = childStartTime + value;
		fblMax = Math.max(fblMax, fblTemp);

		// TODO: fDRT calculation
// fDRT CALCULATION -------------------------------------------------------------------------
		// fDRT(s) = max{tdr(n) + blw(n) for every free node.
		// tdr(n) = min (tdr(n,P) min of all processors
		// tdr(n,P) is earliest schedulable time
		// If nj is a source task, then tdr(nj,P)=0

		int fDRT = schedule.getEarliestSchedulableTime(nextTask,nextProcessor) + childStartTime;

//		child.setCostFunction(Math.max(fblMax, fIdle));

		return Math.max(Math.max(fblMax, fIdle), fDRT);
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
	 * Set the schedule listener for the current listener
	 * @param listener
	 */
	public void setScheduleListener(VisualisationController listener) {
		this.scheduleListener = listener;
	}

	/**
	 * This method updates the GUI schedule with that of the optimal schedule to be shown in the visualisation
	 */
	private void updateGUISchedule () {
		scheduleListener.updateSchedule(optimalSchedule);
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
