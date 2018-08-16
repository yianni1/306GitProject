package scheduling;

import exceptions.NotSchedulableException;
import exceptions.NotDeschedulableException;
import exceptions.TaskException;
import graph.TaskNode;
import javafx.concurrent.Task;

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
    private List<String> taskNames = new ArrayList<String>();
    private Map<String, TaskNode> tasks = new HashMap<String, TaskNode>();

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
    public synchronized void addTask(TaskNode node, int startTime) throws NotSchedulableException {
//        if (node != null) {
        if (startTime < this.getBound()) {
//            System.out.println("this is the bad bound " + this.getBound() + " starttime was " + startTime);
            throw new TaskException("The startTime cannot be lower than the bound");
        }

        taskNames.add(node.getName());
        tasks.put(node.getName(), node);

        node.schedule(startTime, this);
//        }

    }

    /**
     * Removes a task from this node.
     * @param node
     */
    public synchronized void removeTask(TaskNode node) throws NotDeschedulableException {
        tasks.remove(node.getName());
        taskNames.remove(node.getName());
        node.deschedule();
    }

    /**
     * Returns the current bound of this node.
     * @return
     */
    public synchronized int getBound() {
        int bound = 0;
        for (String nodeName : taskNames) {
            TaskNode node = tasks.get(nodeName);
            if (node != null) {
                if (node.getEndTime() > bound) {
                    bound = node.getEndTime();
                }
            }
        }

        return bound;
    }

    /**
     * Returns the nodes that have been scheduled on this processor.
     * @return
     */
    public List<TaskNode> getTasks() {
        List<TaskNode> tasks = new ArrayList<TaskNode>();
        for (String name : taskNames) {
            if (this.tasks.get(name) != null) {
                tasks.add(this.tasks.get(name));
            }
        }
        return tasks;

    }
}


