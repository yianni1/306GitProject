package graph;

import scheduling.Processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Dweep on 26/07/2018
 *The TaskNode Class works as our data structure representation of a node, with the properties of the particular nodes
 * weight, its name whether it has already been scheduled, the start time and the processor the node has been scheduled to which
 * provide the fundamental basis for our scheduler and algo implemenation.
 */
public class TaskNode {
    private int weight;
    private String name;
    private boolean scheduled;
    private int startTime;
    private Processor processor;

    private HashSet<TaskEdge> incomingEdges;
    private HashSet<TaskEdge> outgoingEdges;

    public TaskNode(int weight, String name) {
        this.name = name;
        this.weight = weight;
        this.scheduled = false;
        this.startTime = -1;
        this.processor = null;

        //storing the incoming edges and outgoing edges of the graph which makes it easier to understand the
        //parent and child relationship between the nodes
        incomingEdges = new HashSet<TaskEdge>();
        outgoingEdges = new HashSet<TaskEdge>();
    }

    /**
     *
     * The Status of the node is set to scheduled which assigns the attributes of start time and a processor.
     *
     * @param startTime The time that the node is scheduled in the scheduler
     * @param processor the processor that the particular node is scheduled on
     * @return
     */
    public boolean schedule(int startTime, Processor processor) {
        if (!this.isSchedulable()) {
            return false;
        }
        this.startTime = startTime;
        this.processor = processor;
        this.scheduled = true;
        return true;
    }

    /**
     * Resets this node to its unscheduled state.
     * @return
     */
    public boolean deschedule() {
        if (this.isSchedulable()) {
            return false;
        }
        this.startTime = -1;
        this.processor = null;
        this.scheduled = false;
        return true;
    }

    /**
     * Gets the time at which this node ends.
     * @return
     */
    public int getEndTime() {
        return this.startTime + this.weight;
    }

    /**
     * Adds an incoming edge to this node.
     * @param edge the edge to be added
     */
    public void addIncomingEdge(TaskEdge edge) {
        incomingEdges.add(edge);
    }

    /**
     * Adds an outgoing edge to this node.
     * @param edge the edge to be added
     */
    public void addOutgoingEdge(TaskEdge edge) {
        outgoingEdges.add(edge);
    }

    /**
     * Retrieves incoming edges of this node.
     * @return
     */
    public HashSet<TaskEdge> getIncomingEdges() {
        return incomingEdges;
    }

    /**
     * Retrieves outgoing edges of this node.
     * @return
     */
    public HashSet<TaskEdge> getOutgoingEdges() {
        return outgoingEdges;
    }

    /**
     * Retrieves the weight of this node.
     * @return
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Checks whether this node is scheduled.
     * @return
     */
    public boolean isScheduled() {
        return scheduled;
    }
    
    public void schedule() {
    	scheduled = true;
    }

    /**
     * Checks whether this node is schedulable.
     * @return
     */
    public boolean isSchedulable() {
        if (this.isScheduled()) {
            return false;
        }

        for (TaskEdge e : incomingEdges) {
            if (!e.getStartNode().isScheduled()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the name of this node.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the processor that the node is currently on.
     * @return
     */
    public Processor getProcessor() {
        return processor;
    }
}
