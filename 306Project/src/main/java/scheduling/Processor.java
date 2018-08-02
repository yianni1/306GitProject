package scheduling;

import graph.TaskNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a processor, with a list of tasks
 */
public class Processor {

    private int procID;
    private List<TaskNode> tasks = new ArrayList<TaskNode>();
    private int cost;

    /**
     * The processor with a number
     * @param number
     */
    public Processor(int number) {
        this.procID = number;
    }

    /**
     * Adds a new task, with the node.
     * Called by TaskNode.
     * @param node The node to be added.
     */
    public void addTask(TaskNode node, int time) {
        tasks.add(node);
        node.schedule(cost + time);
        cost = cost + time;
    }


    public int getCost() {
        return cost;
    }
    
    public List<TaskNode> getTasks() {
    	
    	return tasks;
    	
    }
}


