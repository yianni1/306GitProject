package scheduling;

import exceptions.NotDeschedulableException;
import exceptions.NotSchedulableException;
import graph.TaskGraph;
import graph.TaskNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static scheduling.DFBnBScheduler.deepClone;

/**
 * Created by olive on 15/08/2018.
 */
public class ParallelDFS implements Serializable {

    private TaskGraph graph;
    private int upperBound;
    private int depth;
    private Processor currentProcessor;

    // Index of the children of the schedule.
    private List<Integer> nodeIndices;
    private List<Integer> processorIndices;

    private Schedule optimalSchedule;
    private Schedule schedule;
    private List<TaskNode> schedulableNodes;

    private List<TaskNode> initialNodes;



//    public ParallelDFS(TaskGraph graph, int processors) {
//        this.graph = graph;
//
//        initialNodes = new ArrayList<TaskNode>();
//        nodeIndices = new ArrayList<Integer>();
//        processorIndices = new ArrayList<Integer>();
//        schedule = new Schedule(processors, graph);
//        schedulableNodes = schedule.getSchedulableNodes();
//
//        //initialize depth, upperBound, and current time of the schedule
//        depth = 0;
//
//        Schedule greedySchedule = new GreedyScheduler(graph, processors).createSchedule();
//
//        upperBound = greedySchedule.getBound();
//
//        while (greedySchedule.getScheduledNodes().size() > 0) {
//            greedySchedule.removeLastScheduledTask();
//        }
//
//    }


    /**
     * Alternative constructor for parallelisation
     * @param partialSchedule
     * @param upperBound
     */
    public ParallelDFS(Schedule partialSchedule, int upperBound, TaskGraph graph) {
        this.schedule = partialSchedule;
        initialNodes = new ArrayList<TaskNode>();
        nodeIndices = new ArrayList<Integer>();
        processorIndices = new ArrayList<Integer>();
        partialSchedule.getProcessors().size();
        schedulableNodes = partialSchedule.getSchedulableNodes();
        this.upperBound = upperBound;
        this.depth = partialSchedule.getScheduledNodes().size();
//        this.depth = depth;
        this.graph = graph;
    }


//    public Schedule createSchedule() throws NotDeschedulableException, NotSchedulableException {
//        return createSchedule(0);
//    }

    public Schedule createSchedule(int minDepth) {
        //initialize upperBound
        System.out.println("starting dfs");

        TaskNode nextTask = null;
        Processor nextProcessor = null;
        int nodeIndex = 0;
        int processorIndex = 0;

        //Variables to know when all initial nodes have been looped through
        boolean finished = false;

        //Used to not break on first iteration
        boolean initialIteration = true;

        //while there are branches to explore from depth minDepth, keep looping through all branches
        while (depth >= minDepth) {


            while (schedulableNodes.size() > 0) { //while there are still nodes to schedule
                // System.out.println("Searching at depth " + depth + " with bound " + schedule.getBound());
                //if the depth is less than the size of nodeIndices then the depth has been reached before

                //Block for determining if initial nodes have been seen
                if ((depth == minDepth) && (initialIteration == false) ) {

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
                    //If all initial nodes have been seen, set finished to true to finish the algorithm
                    //As the optimal solution has been found
                    if (initialNodes.equals(schedulableNodes)) {
                        finished = true;
                    }

                }

                initialIteration = false;

                if (finished) {
                    break;
                }


                if (depth - minDepth < nodeIndices.size()) {
                    nodeIndex = nodeIndices.get(depth - minDepth); //get the index of the next node at that depth
                    processorIndex = processorIndices.get(depth - minDepth); //get the index of the processor to schedule on
                } else { //otherwise initialise the node index and processor index of that depth as 0
                    nodeIndex = 0;
                    processorIndex = 0;
                    nodeIndices.add(depth - minDepth, nodeIndex);

                    processorIndices.add(depth - minDepth, processorIndex);


                }

                if (nodeIndex < schedulableNodes.size()) { //if there is still schedulable nodes
                    nextTask = schedulableNodes.get(nodeIndex); //get the next available one
                    nextProcessor = schedule.getProcessors().get(processorIndex); //get the processor to schedule on
                } else if (processorIndex < schedule.getProcessors().size() - 1){ //if there is still a processor that we haven't tried to schedule this node on
                    nodeIndices.set(depth - minDepth, 0); //reset the node index
                    processorIndices.set(depth - minDepth, processorIndices.get(depth - minDepth) + 1); //increment the processor index

                    nodeIndex = nodeIndices.get(depth - minDepth);
                    processorIndex = processorIndices.get(depth - minDepth);

                    nextTask = schedulableNodes.get(nodeIndex); //get the next available one
                    nextProcessor = schedule.getProcessors().get(processorIndex); //get the processor to schedule on

                } else { //otherwise no more nodes can be scheduled at this depth
                    nodeIndices.set(depth - minDepth, 0); //reset node index for that depth
                    processorIndices.set(depth - minDepth, 0);

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

                //TODO make better (hypothetical next bound)
                schedule.addTask(nextTask, nextProcessor, schedule.getEarliestSchedulableTime(nextTask, nextProcessor));
                schedulableNodes = schedule.getSchedulableNodes();
                nodeIndices.set(depth - minDepth, nodeIndices.get(depth - minDepth) + 1);


                // kind of pruning
                if (schedule.getBound() > upperBound) {
                    schedule.removeLastScheduledTask();
                    schedulableNodes = schedule.getSchedulableNodes();

                    depth--;

                    if (depth < 0) {
                        break;
                    }

                }

                depth++;
            }

            if (depth < minDepth) {
                break;
            }

            depth--; //go to previous depth

            if (finished) {
                break;
            }
            //TODO clone schedule and set optimal schedule to be this schedule
            if (schedule.getBound() < upperBound || optimalSchedule == null) {
                optimalSchedule = (Schedule) DFBnBScheduler.deepClone(schedule);
                upperBound = schedule.getBound();
//                    System.out.println("Upper Bound updated to " + upperBound);
            }

            if (schedule.getScheduledNodes().size() > 0) {
                schedule.removeLastScheduledTask();
            }

            schedulableNodes = schedule.getSchedulableNodes();

            if (schedulableNodes.size() > graph.getNodes().size()) {
                throw new NullPointerException("Something went wrong");
            }

            nodeIndices.set(depth - minDepth, nodeIndices.get(depth - minDepth) + 1);


        }

//            System.out.println("Solution with bound of " + optimalSchedule.getBound() + " found");
        if (optimalSchedule == null) {
            optimalSchedule = (Schedule) deepClone(schedule);
        }
        return optimalSchedule;

    }
}
