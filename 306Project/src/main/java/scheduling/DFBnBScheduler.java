package scheduling;

import graph.TaskGraph;
import graph.TaskNode;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 28/07/2018.
 * Written by Kevin & Ray.
 */
public class DFBnBScheduler {

        private int upperBound;
        private int depth;
        private Processor currentProcessor;
        private int bound;

        // Index of the children of the schedule.
        private List<Integer> nodeIndices;
        private List<Integer> processorIndices;

        private Schedule optimalSchedule;
        private Schedule schedule;
        private List<TaskNode> schedulableNodes;

        public DFBnBScheduler(TaskGraph graph, int processors) {
            nodeIndices = new ArrayList<Integer>();
            processorIndices = new ArrayList<Integer>();
            schedule = new Schedule(processors, graph);
            schedulableNodes = schedule.getSchedulableNodes();

            //initialize depth, upperBound, and current time of the schedule
            depth = 0;
            upperBound = 0;
            bound = 0;
        }


        public void createSchedule(TaskGraph graph, int processors) {
            //initialize upperBound
            for(TaskNode n: graph.getNodes() ) {
                upperBound = upperBound + n.getWeight();
            }

            TaskNode nextTask = null;
            Processor nextProcessor = null;
            int nodeIndex = 0;
            int processorIndex = 0;

            //while there are branches to explore from depth 0, keep looping through all branches
            while (depth >= 0) {
                while (schedulableNodes.size() > 0) { //while there are still nodes to schedule
                    //if the depth is less than the size of nodeIndices then the depth has been reached before
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
                        bound = 0;

                        depth--; //go to previous depth

                        //if there are scheduled nodes
                        if (schedule.getScheduledNodes().size() > 0) {
                            schedule.removeLastScheduledTask(); //remove the last scheduled task from the most recent depth

                            schedule.updateSchedulableNodes(nextTask);

                            //TODO update bound
                            //TODO get schedulable nodes
                        }

                    }

                    schedule.addTask(nextTask, nextProcessor, schedule.getEarliestSchedulableTime(nextTask, nextProcessor));











                    depth++;
                }







            }

        }



}
