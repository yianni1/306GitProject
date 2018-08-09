package scheduling;

import exceptions.NotSchedulableException;
import graph.TaskGraph;
import graph.TaskNode;

import java.util.List;

/**
 * Created on 3/0/8/2018 by Ray and Kevin
 */

public class GreedyScheduler implements Scheduler {

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

    public Schedule createSchedule() throws NotSchedulableException {
    	
        //While there are still nodes to schedule
        while (schedulableNodes.size() > 0) {
            //TODO find a better a way to initalize this
            TaskNode nextNode = schedulableNodes.get(0);
            Processor nextProcessor = schedule.getProcessors().get(0);
            int nextStartTime = schedule.getEarliestSchedulableTime(nextNode, nextProcessor);
            int nextEndTime = nextStartTime + nextNode.getWeight();


            //checking the earliest end time and then picks the node to schedule based on that
            for (Processor p : schedule.getProcessors()) {
                for (TaskNode n: schedulableNodes) {
                
                    int tentativeStartTime = schedule.getEarliestSchedulableTime(n, p);
                    int tentativeEndTime = tentativeStartTime + n.getWeight();
                    
                    if (tentativeEndTime < nextEndTime) {
                        nextStartTime = tentativeStartTime;
                        nextEndTime = tentativeEndTime;
                        nextNode = n;
                        nextProcessor = p;
                    }

                }
            }

            //add that node to the schedule
            schedule.addTask(nextNode, nextProcessor, nextStartTime);
            schedulableNodes = schedule.getSchedulableNodes();
        }

        return schedule;

    }
}
