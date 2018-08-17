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

                // System.out.println("Searching at depth " + depth + " with bound " + schedule.getBound());
                //if the depth is less than the size of nodeIndices then the depth has been reached before

                //Block for determining if initial nodes have been seen
                if ((depth == minDepth) && (initialIteration == false) ) {
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


                if (depth < nodeIndices.size()) {
                    nodeIndex = nodeIndices.get(depth); //get the index of the next node at that depth
                    processorIndex = processorIndices.get(depth); //get the index of the processor to schedule on
                } else { //otherwise initialise the node index and processor index of that depth as 0
                    nodeIndex = 0;
                    processorIndex = 0;
                    nodeIndices.add(depth, nodeIndex);

                    processorIndices.add(depth, processorIndex);


                }

                if (nodeIndex < schedulableNodes.size()) { //if there is still schedulable nodes
                    nextTask = schedulableNodes.get(nodeIndex); //get the next available one
                    nextProcessor = schedule.getProcessors().get(processorIndex); //get the processor to schedule on
                } else if (processorIndex < schedule.getProcessors().size() - 1){ //if there is still a processor that we haven't tried to schedule this node on
                    nodeIndices.set(depth, 0); //reset the node index
                    processorIndices.set(depth, processorIndices.get(depth) + 1); //increment the processor index

                    nodeIndex = nodeIndices.get(depth);
                    processorIndex = processorIndices.get(depth);

                    nextTask = schedulableNodes.get(nodeIndex); //get the next available one
                    nextProcessor = schedule.getProcessors().get(processorIndex); //get the processor to schedule on

                } else { //otherwise no more nodes can be scheduled at this depth
                    nodeIndices.set(depth, 0); //reset node index for that depth
                    processorIndices.set(depth, 0);

                    depth--; //go to previous depth

                    if (depth < minDepth) {
                        break;
                    }

                    //if there are scheduled nodes
                    if (schedule.getScheduledNodes().size() > minDepth) {
                        schedule.removeLastScheduledTask(); //remove the last scheduled task from the most recent depth

                        schedulableNodes = schedule.getSchedulableNodes(); //get schedulable nodes
                    }
                    continue;
                }

                //TODO make better (hypothetical next bound)
                schedule.addTask(nextTask, nextProcessor, schedule.getEarliestSchedulableTime(nextTask, nextProcessor));
                schedulableNodes = schedule.getSchedulableNodes();
                nodeIndices.set(depth, nodeIndices.get(depth) + 1);

                // kind of pruning
                if (schedule.getBound() > upperBound && depth != minDepth) {
                    try {
//                    System.out.println("depth is " + depth + " mindepth is " + minDepth);
                        schedule.removeLastScheduledTask();
                        schedulableNodes = schedule.getSchedulableNodes();

                        depth--;

                        if (depth < minDepth ) {
                            break;
                        }
                    }
                    catch (NullPointerException npe) {
                        npe.printStackTrace();
                        System.out.println(depth);
                    }

                }

                depth++;
            }



            depth--; //go to previous depth

            if (depth < minDepth) {
                break;
            }

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

            nodeIndices.set(depth, nodeIndices.get(depth) + 1);


        }

//            System.out.println("Solution with bound of " + optimalSchedule.getBound() + " found");
        if (optimalSchedule == null) {
            optimalSchedule = (Schedule) deepClone(schedule);
            upperBound = optimalSchedule.getBound();
        }


        return optimalSchedule;

    }

}
