package graph;

import exceptions.NotSchedulableException;
import exceptions.NotDeschedulableException;
import scheduling.Processor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * The TaskNode is a class which encapsulates the node in the graph as well as in the tasks in the solution tree
 */
public class TaskNode implements Serializable {

    //the weight of the node
    private int weight;

    //the name identification for the node
    private String name;

    //Checking if the tasknode is scheduled
    private boolean scheduled;

    //checking the start time which defaults to - 1 if it it is not_scheduled
    private int startTime;

    //the processor on which the task is scheduled
    private Processor processor;

    //the set to store the incoming edges
    private HashSet<TaskEdge> incomingEdges;

    //the set to store the outgoing edges
    private HashSet<TaskEdge> outgoingEdges;

    /**
     * The TaskNode Constructor creates a TaskNode object through the usage of the parameter of weight and name storing
     * various other features for that node which allow it to be scheduled
     * @param weight of the node
     * @param name of the node
     */
    public TaskNode(int weight, String name) {
        this.name = name;
        this.weight = weight;
        this.scheduled = false;
        this.startTime = -1;
        this.processor = null;

        //storing the incoming and outgoing edges of the node
        incomingEdges = new HashSet<TaskEdge>();
        outgoingEdges = new HashSet<TaskEdge>();
    }
    
    /**
     *
     * The Node becomes scheduled which updates its status and provides the processor and the start - time
     *
     * @param startTime The time that the node is scheduled in the scheduler
     * @param processor the processor that the particular node is scheduled on
     * @return
     */
    public boolean schedule(int startTime, Processor processor) throws NotSchedulableException {

        //checking if node is schedulable
        if (!this.isSchedulable()) {

            //throws an exception if the node is not able to be scheduled
            throw new NotSchedulableException();
        }


        this.startTime = startTime;
        this.processor = processor;
        this.scheduled = true;
        return true;
    }

    /**
     * Changes the node back to an unscheduled state
     * @return
     */
    public boolean deschedule() throws NotDeschedulableException {

        //checking if the node can be descheduled if it cannot be descheduled then
        if (!this.isDeschedulable()) {

            //throws an exception
            throw new NotDeschedulableException();
        }

        this.startTime = -1;
        this.processor = null;
        this.scheduled = false;
        return true;
    }

    /**
     * Gets the time when the node ends
     * @return
     */
    public int getEndTime() {
        return this.startTime + this.weight;
    }

    /**
     * Gets the start time of the node but the prerequisite for this is that the node needs to be scheduled
     * @return
     */
    public int getStartTime() {
    	return this.startTime;
    }

    /**
     * Adds an incoming edge to the node , which serves as its parent
     * @param edge the incoming edge that is added
     */
    public void addIncomingEdge(TaskEdge edge) {
        incomingEdges.add(edge);
    }

    /**
     * Adds the outgoing edge to the node, which serves as the nodes child
     * @param edge the outgoing edge which is added
     */
    public void addOutgoingEdge(TaskEdge edge) {
        outgoingEdges.add(edge);
    }

    /**
     * Retrieves a list of all the incoming edges
     * @return
     */
    public HashSet<TaskEdge> getIncomingEdges() {
        return incomingEdges;
    }

    /**
     * Retrieves list of all the outgoing edges
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

        //check if the startnode is scheduled
        for (TaskEdge e : incomingEdges) {
            if (!e.getStartNode().isScheduled()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the node is deschedulable, can only deschedule if the children of the node haven't been scheduled
     */
    public boolean isDeschedulable() {
        if (!this.isScheduled()) {
            return false;
        }

        //get the end node and check if it is scheduled which serves as the outgoing edge
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
