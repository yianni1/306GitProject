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

    private int number;
    private List<Task> tasks = new ArrayList<Task>();
    private int cost;

    /**
     * The processor with a number
     * @param number
     */
    public Processor(int number) {
        this.number = number;
    }

    /**
     * Adds a new task, with the node, startTime and endTime
     * @param node
     * @param startTime
     * @param endTime
     */
    public void addTask(TaskNode node, int startTime, int endTime) {
        Task task = Task.getTask(node); //Creates/returns the task
        task.scheduleTask(startTime, endTime, this);
        tasks.add(task);
        if (cost < endTime) {
            cost = endTime;
        }
    }


    int getCost() {
        return cost;
    }
}


class Task {
    private TaskNode node;
    private int startTime;
    private int endTime;
    private Processor processor; //The processor it's on

    private static Map<TaskNode, Task> nodes = new HashMap<TaskNode, Task>();



    /**
     * The task that will be done
     * Private because we do not want two
     * tasks with the same tasknode
     * @param node
     */
    private Task(TaskNode node) {
        this.node = node;

        nodes.put(node, this);
    }

    /**
     *
     * Schedules a task.
     * @param startTime
     * @param endTime
     * @param processor
     */
    public void scheduleTask(int startTime, int endTime, Processor processor) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.processor = processor;
    }

    /**
     * Returns a task that encapsulates a given node
     * @param node
     * @return
     */
    public static Task getTask(TaskNode node) {
        if (nodes.get(node) == null) {
            return new Task(node);
        }
        else {
            return nodes.get(node);
        }
    }








}
