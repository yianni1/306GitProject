package scheduling;

import exceptions.NotSchedulableException;
import graph.TaskGraph;
import graph.TaskNode;

import java.util.List;

/**
 * Created on 3/0/8/2018 by Ray and Kevin
 * Cleaned by Dweep and Oliver
 * This class generates a greedy solution to scheduling based on the earliest start time for each of the tasks to be
 * placed on the schedules while iterating through the solution treeS
 */

public class GreedyScheduler implements Scheduler {

    //variables associated with scheduling
	private List<TaskNode> schedulableNodes;
	private Schedule schedule;
	
	/**
     * Constructor takes in a graph and the number of processors
     * @param graph
     * @param processors
     */
    public GreedyScheduler(TaskGraph graph, int processors) {
    	schedule = new Schedule(processors, graph);
    	schedulableNodes = schedule.getSchedulableNodes();
    }

    /**
     * Generates a greedy schedule relatively quickly but the solution is not considered to be optimal but it is a
     * valid schedule
     * @return A "guess" for the optimal schedule or a valid schedule
     * @throws NotSchedulableException //where the task was not able to added to the schedule
     */
    public Schedule createSchedule() throws NotSchedulableException {
    	
        //While there are still nodes to schedule
        while (schedulableNodes.size() > 0) {

            //intiialize the next node, processor, start-time and end-time
            TaskNode nextNode = schedulableNodes.get(0);
            Processor nextProcessor = schedule.getProcessors().get(0);
            int nextStartTime = schedule.getEarliestSchedulableTime(nextNode, nextProcessor);
            int nextEndTime = nextStartTime + nextNode.getWeight();


            //Go through all nodes and check earliest schedulable time on each processor to find next best node to schedule
            for (Processor p : schedule.getProcessors()) {
                for (TaskNode n: schedulableNodes) {

                    //get the earliest start time of the node and the end time
                    int tentativeStartTime = schedule.getEarliestSchedulableTime(n, p);
                    int tentativeEndTime = tentativeStartTime + n.getWeight();

                    //if the stored endtime is less than the next end time, then it changes its value
                    if (tentativeEndTime < nextEndTime) {
                        nextStartTime = tentativeStartTime;
                        nextEndTime = tentativeEndTime;
                        nextNode = n;
                        nextProcessor = p;
                    }

                }
            }

            //add the particular node to the schedule
            schedule.addTask(nextNode, nextProcessor, nextStartTime);
            schedulableNodes = schedule.getSchedulableNodes();
        }

        //returns a valid and sometimes optimal schedule
        return schedule;

    }
}
