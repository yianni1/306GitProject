package scheduling;

import exceptions.NotSchedulableException;
import exceptions.NotDeschedulableException;
import exceptions.TaskException;
import graph.TaskNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a processor, with a list of tasks
 */
public class Processor implements Serializable {

    private int procID; //The ID of the processor
    private int bound = 0;
    private List<TaskNode> tasks = new ArrayList<TaskNode>(); //The list of tasks on that processor

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

    public void setProcID(int procID) {
        this.procID = procID;
    }

    /**
     * Adds a new task, with the node.
     * Called by TaskNode.
     * @param node The node to be added.
     * @param startTime The starttime
     */
    public void addTask(TaskNode node, int startTime) throws NotSchedulableException {
        if (startTime < this.getBound()) {
            throw new TaskException("The startTime cannot be lower than the bound");
        }

        tasks.add(node);

        bound = startTime + node.getWeight();

        node.schedule(startTime, this);

    }

    /**
     * Removes a task from this node.
     * @param node
     */
    public void removeTask(TaskNode node) throws NotDeschedulableException {
        tasks.remove(node);
        if (tasks.size() > 0) {
            bound = tasks.get(tasks.size() - 1).getEndTime();
        } else {
            bound = 0;
        }
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


