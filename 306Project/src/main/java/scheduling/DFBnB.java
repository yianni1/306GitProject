package scheduling;

import graph.TaskGraph;
import graph.TaskNode;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 28/07/2018.
 * Written by Kevin & Ray.
 */
public class DFBnB {

        private int upperBound;
        private int depth;
        private Processor currentProcessor;
        private int bound;

        // Index of the children of the schedule.
        private List<Integer> nodeIndices = new ArrayList<Integer>();
        private List<Integer> processorIndices = new ArrayList<Integer>();

        private Schedule optimalSchedule;
        private Schedule schedule;
        private List<TaskNode> schedulableNodes;


        public void createSchedule(TaskGraph graph, int processors) {
            //create new schedule with number of processors and initialize the schedulable nodes
            schedule = new Schedule(processors, graph);

            //initialize depth, upperBound, and current time of the schedule
            depth = 0;
            upperBound = 0;
            bound = 0;

            //initialize upperBound
            for(TaskNode n: graph.getNodes() ) {
                upperBound = upperBound + n.getWeight();
            }

            TaskNode node;

            //while there are branches to explore from depth 0, keep looping through all branches
            while (depth >= 0) {
                schedulableNodes = schedule.getSchedulableNodes(); //get all nodes that can be scheduled

                while (schedulableNodes.size() > 0) { //while there are still nodes to schedule
                    int nodeIndex;

                    //if the depth is less than the size of nodeIndices then the depth has been reached before
                    if (depth < nodeIndices.size()) {
                        nodeIndex = nodeIndices.get(depth); //get the index of the next node at that depth
                    } else { //otherwise initialise the node index of that depth as 0
                        nodeIndex = 0;
                        nodeIndices.add(depth, 0);
                    }

                    if (nodeIndex < schedulableNodes.size()) { //if there is still schedulable nodes
                        node = schedulableNodes.get(nodeIndex); //get the next available one
                    } else { //otherwise no more nodes can be scheduled at this depth
                        nodeIndices.set(depth, 0); //reset node index for that depth
                        bound = 0;

                        depth--; //go to previous depth

                        //if there are scheduled nodes
                        if (schedule.getScheduledNodes().size() > 0) {
                            schedule.removeLastScheduledTask(); //remove the last scheduled task from the most recent depth

                            //TODO update bound
                            //TODO get schedulable nodes
                        }

                    }


                    TaskNode currentTask = schedulableNodes.get(nodeIndices.get(depth));








                    depth++;
                }







            }

        }



}
