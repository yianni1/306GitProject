package scheduling;

import graph.TaskNode;

import java.util.ArrayList;
import java.util.List;

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
        tasks.add(new Task(node, startTime, endTime));
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

    /**
     * The task that will be done
     * @param node
     * @param startTime
     * @param endTime
     */
    public Task(TaskNode node, int startTime, int endTime) {
        this.node = node;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
