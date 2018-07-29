package scheduling;

import graph.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a processor, with a list of tasks
 */
public class Processor {


    private List<Task> tasks = new ArrayList<Task>();
    private int cost;

    /**
     * Adds a new task, with the node, startTime and endTime
     * @param node
     * @param startTime
     * @param endTime
     */
    public void addTask(Node node, int startTime, int endTime) {
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
    private Node node;
    private int startTime;
    private int endTime;

    /**
     * The task that will be done
     * @param node
     * @param startTime
     * @param endTime
     */
    public Task(Node node, int startTime, int endTime) {
        this.node = node;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
