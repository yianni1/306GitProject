package graph;

import exceptions.NotSchedulableException;
import exceptions.NotDeschedulableException;
import scheduling.Processor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This class is used to encapsulate a node on the input graph,
 * as well as a task on the final solutionTree.
 */
public class TaskNode implements Serializable {
    private int weight; //The weight of the node
    private String name; //The name of the node, used to identify it

    private boolean scheduled; //Whether the TaskNode is scheduled
    private int startTime; //The starttime of the task. -1 if it is not scheduled
    private Processor processor; //The processor on which it is scheduled
    private int costFunction = Integer.MAX_VALUE; //The cost function

    private HashSet<TaskEdge> incomingEdges; //The set of incoming edges
    private HashSet<TaskEdge> outgoingEdges; //The set of outgoing edges

    /**
     * Constructor creates a TaskNode object with a given name and weight
     * @param weight
     * @param name
     */
    public TaskNode(int weight, String name) {
        this.name = name;
        this.weight = weight;
        this.scheduled = false;
        this.startTime = -1;
        this.processor = null;
        incomingEdges = new HashSet<TaskEdge>();
        outgoingEdges = new HashSet<TaskEdge>();
    }

    /**
     * Returns the cost function of the node
     * @return
     */
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
            throw new NotSchedulableException(); //Throws an exception of the node is not schedulable
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

    /**
     * Returns the start time of the node, provided it is  scheduled
     * @return
     */
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
