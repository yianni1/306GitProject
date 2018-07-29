package scheduling;

import graph.TaskGraph;
import graph.TaskNode;

import javax.xml.soap.Node;
import java.util.ArrayList;
import java.util.List;

/**
 * This class shows a partial Schedule to the problem. It will be a node on the Schedule tree
 */
public class Schedule {

    private List<Processor> processors = new ArrayList<Processor>();
    private int cost; //The cost for this Schedule
//    private List<Schedule> children = new ArrayList<Schedule>(); //The children of this Schedule

    /**
     * Returns a partial schedule
     * @param processors
     */
    public Schedule(List<Processor> processors) {
        this.processors = processors;

        //Calculating cost
        cost = 0;
        for (Processor p : this.processors) {
            if (p.getCost() > cost) {
                this.cost = p.getCost();
            }
        }
    }

    /**
     * Returns all the children of a given partial schedule
     * @param availableNodes
     * @return
     */
    public List<Schedule> createChildren(List<TaskNode> availableNodes) {
        List<Schedule> result = new ArrayList<Schedule>();
        return null;
    }
}
