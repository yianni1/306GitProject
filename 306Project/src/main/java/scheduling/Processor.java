package scheduling;

import exceptions.NotSchedulableException;
import exceptions.NotScheduledException;
import graph.TaskNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a processor, with a list of tasks
 */
public class Processor implements Serializable {

    private int procID;
    private List<TaskNode> tasks = new ArrayList<TaskNode>();
    private int bound;

    /**
     * The processor with a number
     * @param number
     */
    public Processor(int number) {
        this.procID = number;
    }

    /**
     * Gets the id of the processor
     * @return
     */
    public int getID() {
    	return procID;
    }
    
    /**
     * Adds a new task, with the node.
     * Called by TaskNode.
     * @param node The node to be added.
     * @param startTime The starttime
     */
    public void addTask(TaskNode node, int startTime) throws NotSchedulableException {
        tasks.add(node);

        if (startTime < bound) {
            throw new TaskException("The startTime cannot be lower than the bound");
        }
        node.schedule(startTime, this);
        bound = startTime + node.getWeight();

    }

    /**
     * Removes a task from this node.
     * @param node
     */
    public void removeTask(TaskNode node) throws NotScheduledException {
        bound = node.getEndTime() - node.getWeight();
        tasks.remove(node);
        node.deschedule();
    }

    /**
     * Returns the current bound of this node.
     * @return
     */
    public int getBound() {
        return bound;
    }

    /**
     * Returns the nodes that have been scheduled on this processor.
     * @return
     */
    public List<TaskNode> getTasks() {
    	
    	return tasks;
    	
    }
}


