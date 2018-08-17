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
 * This implements the DFS algorithim for searching an optimal schedule in the parallel with multiple processors
 * Refactored by Dweep
 */
public class ParallelDFS implements Serializable {

    //variables for DFS
    private TaskGraph graph;
    private int upperBound;
    private int depth;


    //Index of the children nodes which need to be scheduled , and indices involving the processor
    private List<Integer> nodeIndices;
    private List<Integer> processorIndices;

    //the variables involving th schedule
    private Schedule optimalSchedule;
    private Schedule schedule;
    private List<TaskNode> schedulableNodes;

    //variable for the initial nodes of the graph
    private List<TaskNode> initialNodes;


    /**
     * OLD CONSTRUCTOR FOR THE DFS TAKE FROM THE NORMAL DFS FUNCTION
     */
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
     * The Constructor for the parallization DFS, it takes the the following inputs in order
     * to the call the aglorithim and create the optimal schedule
     * @param partialSchedule -> the partial schedule that has been generated
     * @param upperBound
     * @param graph
     * @param depth
     */
    public ParallelDFS(Schedule partialSchedule, int upperBound, TaskGraph graph, int depth) {

        //creating a structure for the intial nodes
        initialNodes = new ArrayList<TaskNode>();

        //strcuture for nodes and processor indices
        nodeIndices = new ArrayList<Integer>();
        processorIndices = new ArrayList<Integer>();

        //storing , checking for the processors, and the scheduable nodes for a partial schedule
        this.schedule = partialSchedule;
        partialSchedule.getProcessors().size();
        schedulableNodes = partialSchedule.getSchedulableNodes();

        //storing in global variables
        this.upperBound = upperBound;
        this.depth = depth;
        this.graph = graph;
    }


//    public Schedule createSchedule() throws NotDeschedulableException, NotSchedulableException {
//        return createSchedule(0);
//    }

    /**
     * This method is used to create the optimal schedule by running the DFS algortihim this invlvoves first going through the
     * BFS search to check the number of cores and then going through DFS with each of the nodes on the branches that are in the
     * graph
     * @param minDepth
     * @return
     */
    public Schedule createSchedule(int minDepth) {

        //initialize the upperbound which allows algorithim iteration
        TaskNode nextTask = null;
        Processor nextProcessor = null;
        int nodeIndex = 0;
        int processorIndex = 0;

        int NodeIndex = 0;
        int ProcessorIndex = 0;

        //adding the nodes which are run through the BFS search into the Indices datastructures
        for (int i = 0; i < minDepth; i++) {
            nodeIndices.add(i);
            processorIndices.add(i);
        }

        //this is variable to know when all the intial nodes have been traversed through
        boolean finished = false;

        //to not break the loop when it occurs in the first iteration
        boolean initialIteration = true;

        //while there are branches to explore from depth minDepth, keep looping through all branches
        while (depth >= minDepth) {


            while (schedulableNodes.size() > 0) { //while there are still nodes to schedule

                //print statements for looking at the outputs that are produced
                // System.out.println("Searching at depth " + depth + " with bound " + schedule.getBound());


                //generating the solution tree through looking at all the node and the branches
                //checking if there are still anymore scheduble nodes
                if (nodeIndex < schedulableNodes.size()) {

                    //get the next available one
                    nextTask = schedulableNodes.get(nodeIndex);

                    //get the processor to schedule on
                    nextProcessor = schedule.getProcessors().get(processorIndex);


                } else if (ProcessorIndex < schedule.getProcessors().size() - 1) {

                    //reset the node Index to that of zero
                    nodeIndices.set(depth, 0);

                    //change in the increment of the processor index
                    processorIndices.set(depth, processorIndices.get(depth) + 1);

                    //get the depth and processor index of the node
                    nodeIndex = nodeIndices.get(depth);
                    processorIndex = processorIndices.get(depth);

                    //get the next available node
                    nextTask = schedulableNodes.get(nodeIndex);

                    //get the processor to schedule on
                    nextProcessor = schedule.getProcessors().get(processorIndex);

                } else { //there are no more nodes that can be scheduled at this depth

                    //reset node index for that depth
                    nodeIndices.set(depth, 0);

                    //reset processor index for the depth
                    processorIndices.set(depth, 0);

                    //go to the previous iterative depth
                    depth--;

                    if (depth < minDepth) {
                        break;
                    }

                    //if there are scheduled nodes the remove the last scheduled nodes and then
                    //go to the previous depth
                    if (schedule.getSchedulableNodes().size() > minDepth) {

                        //remove the last scheduled task from the most recent depth
                        schedule.removeLastScheduledTask();

                        //get all the scheduable nodes
                        schedulableNodes = schedule.getSchedulableNodes();
                    }

                    continue;

                }


                //add the task to the schedule based on the earliest scheduable time
                schedule.addTask(nextTask, nextProcessor, schedule.getEarliestSchedulableTime(nextTask, nextProcessor));

                //get the current schedule-able nodes and change the depth
                schedulableNodes = schedule.getSchedulableNodes();
                nodeIndices.set(depth, nodeIndices.get(depth) + 1);

                //prunning occuring based on upperbound value being greater than the
                //upperbound then it skips those values
                if (schedule.getBound() > upperBound && depth != minDepth) {
                    try {
                        //skips the task and checks for the scheduble nodes
                        schedule.removeLastScheduledTask();
                        schedulableNodes = schedule.getSchedulableNodes();

                        depth--;

                        //if the depth is less than the mindepth then break
                        if (depth < minDepth) {
                            break;
                        }

                        //catching a null pointer exception based on a null value for the nodes
                    } catch (NullPointerException npe) {

                        //print the stacktrace and the depth
                        npe.printStackTrace();
                        System.out.println(depth);

                    }


                }

            }


            //iterates back to the pervious depth
            depth--;

            if (depth < minDepth) {
                break;
            }

            if (finished) {
                break;
            }

            //clones the schedule and sets the optimal schedule to the current schedule
            //checking if the bound is lower than the upper bound and the optimal schedule
            //is set to null
            if (schedule.getBound() < upperBound || optimalSchedule == null) {
                //create a deepclone of the schedule
                optimalSchedule = (Schedule) DFBnBScheduler.deepClone(schedule);
                upperBound = schedule.getBound();

                System.out.println("Upper Bound is update to" + upperBound);
            }

            //if the number of scheduleable nodes is greater than zero than iterate back
            //through the tree
            if (schedule.getSchedulableNodes().size() > 0) {

                //removes the last schedulable nodes
                schedule.removeLastScheduledTask();

            }

            //get the other schedulable nodes
            schedulableNodes = schedule.getSchedulableNodes();

            //if the amount of scheduable nodes size is greater than that of the graph size
            if (schedulableNodes.size() > graph.getNodes().size()) {

                //throw an exception if this is the case
                throw new NullPointerException("Something is wrong with code , check schedulable nodes");

            }

            nodeIndices.set(depth, nodeIndices.get(depth) + 1);

        }

        //print out the optimal schedule after it has been found
        //System.out.println("Solution with the bound of " + optimalSchedule.getBound() + "found");

        //if the optimal schedule is null then we get a deepclone fo the schedule and set an upperbound
        //as the solution
        if (optimalSchedule == null) {

            //get a deepclone of the schedule
            optimalSchedule = (Schedule) deepClone(schedule);
            //get the upperbound value as the bound
            upperBound = optimalSchedule.getBound();

        }

        return optimalSchedule;
    }
}
