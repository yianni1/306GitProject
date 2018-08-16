package graph;

import exceptions.NotSchedulableException;
import exceptions.NotDeschedulableException;
import scheduling.Processor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TaskNode implements Serializable {
    private int weight;
    private String name;
    private boolean scheduled;
    private int startTime;
    private Processor processor;
    private int costFunction;
    public boolean isVisistedInDFS; // USED FOR criticalPath method in DFBnB

    private HashSet<TaskEdge> incomingEdges;
    private HashSet<TaskEdge> outgoingEdges;

    public TaskNode(int weight, String name) {
    	isVisistedInDFS = false;
        this.name = name;
        this.weight = weight;
        this.scheduled = false;
        this.startTime = -1;
        this.processor = null;
        incomingEdges = new HashSet<TaskEdge>();
        outgoingEdges = new HashSet<TaskEdge>();
    }

    public int getCostFunction() {
    	return costFunction;
    }
    
    public void setCostFunction(int costFunction) {
    	this.costFunction = costFunction;
    }
    
    
    /**
     *
     * The Status of the node is set to scheduled which assigns the attributes of start time and a processor.
     *
     * @param startTime The time that the node is scheduled in the scheduler
     * @param processor the processor that the particular node is scheduled on
     * @return
     */
    public boolean schedule(int startTime, Processor processor) throws NotSchedulableException {
        if (!this.isSchedulable()) {
            // System.out.println("Scheduling task " + this.name + " is invalid.");
            throw new NotSchedulableException();
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
    public boolean deschedule() throws NotDeschedulableException {
        if (!this.isDeschedulable()) {
            // System.out.println("Unscheduled node attempted to be descheduled.");
            throw new NotDeschedulableException();
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

    public int getStartTime() {
    	return this.startTime;
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
     * Checks if the node is deschedulable (it's children are not scheduled).
     */
    public boolean isDeschedulable() {
        if (!this.isScheduled()) {
            return false;
        }

        for (TaskEdge e: outgoingEdges) {
            if (e.getEndNode().isScheduled()) {
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
