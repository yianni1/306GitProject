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
 */
public class DFBnBScheduler implements Scheduler{

	private VisualisationController scheduleListener;

	private TaskGraph graph;
	protected int upperBound;
	protected int depth;
	protected int minDepth;
	protected long numPaths;
	protected long branchesPruned;
	private int processors;

	// Index of the children of the schedule.
	private List<Integer> nodeIndices;
	private List<Integer> processorIndices;

	protected Schedule optimalSchedule;
	protected Schedule schedule;
	protected List<TaskNode> schedulableNodes;

	private List<TaskNode> initialNodes;

	private Map<String, Boolean> combinations = new HashMap<>();

	public DFBnBScheduler(TaskGraph graph, int processors) {
		this.graph = graph;
		this.processors = processors;

        //storing the initial nodes, indexs for the nodes and processors
        initialNodes = new ArrayList<>();
        nodeIndices = new ArrayList<>(Collections.nCopies((graph.getNodes().size()), 0)); //depth of all nodes in the nodeIndices array is set to zero
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
            Schedule greedySchedule = new GreedyScheduler(graph, processors).createSchedule();

            optimalSchedule = (Schedule) deepClone(greedySchedule); //set greedy schedule to be initial optimal schedule

            upperBound = greedySchedule.getBound();

            while (greedySchedule.getScheduledNodes().size() > 0) {
                greedySchedule.removeLastScheduledTask();
            }
        }

		String[] namesArray = new String[schedulableNodes.size()];
		String[] resultArray = new String[schedulableNodes.size()];
		Map<String,Boolean> combinations= new HashMap<>();

		for (int i=0; i<schedulableNodes.size(); i++) {
			namesArray[i]=schedulableNodes.get(i).getName();
		}

		generateCombinations(namesArray, processors, 0, new String[processors]);

	}



	/**
	 * Returns the schedule according to DFSBnB
	 * @return Schedule object for the schedule
	 * @throws NotDeschedulableException Task not deSchedulable
	 * @throws NotSchedulableException Task not Schedulable
	 */
	public Schedule createSchedule() throws NotDeschedulableException, NotSchedulableException {

		updateSchedule();
		updateNumPaths(true);

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

			while (schedulableNodes.size() > 0) { //while there are still nodes to schedule

				//Determine whether initial nodes have been repeated
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

					if (depth < minDepth) {
						break;
					}

					//if there are scheduled nodes
					if (schedule.getScheduledNodes().size() > 0) {
						schedule.removeLastScheduledTask(); //remove the last scheduled task from the most recent depth

						schedulableNodes = schedule.getSchedulableNodes(); //get schedulable nodes
					}
					continue;
				}

				int est = schedule.getEarliestSchedulableTime(nextTask, nextProcessor);

				// Don't add the task to the processor if the upper bound will be higher
				if (est + nextTask.getWeight() < upperBound && !skip) {
					schedule.addTask(nextTask, nextProcessor, est);
					schedulableNodes = schedule.getSchedulableNodes();
					nodeIndices.set(depth, nodeIndices.get(depth) + 1);
					depth++;
				} else {	// Pruning
					nodeIndices.set(depth, nodeIndices.get(depth) + 1);
					branchesPruned++;
					updateBranchesPruned();

				}
			}

			//Ending the algorithm if the depth is lower than the
			//Minimum depth
			if (depth < minDepth) {
				break;
			}

			depth--; //go to previous depth

			if (finished) {
				break;
			}

			//Updating the optimal schedule
			if (schedule.getBound() < upperBound || optimalSchedule == null) {
				optimalSchedule = (Schedule) deepClone(schedule); //Creating a clone for the optimal schedule
				upperBound = schedule.getBound();
				//update visualisation with new optimal schedule
				updateSchedule();
				updateNumPaths(true);
			}

			//If there are scheduled nodes, we remove the last one and
			//Go back up on the tree
			if (schedule.getScheduledNodes().size() > 0) {
				schedule.removeLastScheduledTask();
				numPaths++;
				updateNumPaths(false);
			}

			schedulableNodes = schedule.getSchedulableNodes();

		}
		if (optimalSchedule == null) {
			System.out.println("No solution better than initial upper bound found on this branch.");
		} else {
			System.out.println("Solution with bound of " + optimalSchedule.getBound() + " found");
		}

		//Updating the fields
		updateSchedule();
		updateNumPaths(true);
		updateBranchesPruned();
		finish();

		return optimalSchedule;

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

    public void generateCombinations(String[] arr, int len, int startPosition, String[] result){
        if (len == 0){
            System.out.println(Arrays.toString(result));
            combinations.put(Arrays.toString(result) + Arrays.toString(new int[processors]), false);
            return;
        }
        for (int i = startPosition; i <= arr.length-len; i++){
            result[result.length - len] = arr[i];
            generateCombinations(arr, len-1, i+1, result);
        }
    }

	public void setScheduleListener(VisualisationController listener) {
		this.scheduleListener = listener;
	}

	/**
	 * Notifies listener of new optimal schedule.
	 */
	public void updateSchedule() {
		if (scheduleListener != null) {
			scheduleListener.updateSchedule(optimalSchedule);
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
		if (scheduleListener != null && (System.currentTimeMillis() % 100 == 0)) {
            scheduleListener.updateBranchesPruned(branchesPruned);
		}

    }

    /**
     * Updates listener with number of paths searched.
     */
    public void updateNumPaths(boolean forced) {
		if (scheduleListener != null && ((System.currentTimeMillis() % 100 == 0) || forced)) {
			scheduleListener.updateNumPaths(numPaths);
        }
    }

	/**
	 * Updates listener that algorithm has finished.
	 */
	public void finish() {
		if (scheduleListener != null) {
			scheduleListener.finish();
		}
	}



}
