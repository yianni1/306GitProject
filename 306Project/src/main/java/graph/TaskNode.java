package graph;

import scheduling.Processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
        incomingEdges = new HashSet<TaskEdge>();
        outgoingEdges = new HashSet<TaskEdge>();
    }

    /**
     *
     * Sets the status of this node to scheduled, assigning a start time and a processor.
     *
     * @param startTime the time at which the node is scheduled
     * @param processor the processor that this node has been scheduled on
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
     * This should only be run if the task is schedulable.
     * It should return the earliest schedulable time.
     *
     * @return
     */
    public int getEarliestSchedulableTime(Processor p) {
        int earliestStartTime = -1;
        if (this.isSchedulable()) {
            for (TaskEdge e : this.getIncomingEdges()) {
                int endTime = e.getStartNode().getEndTime();
                if (endTime > earliestStartTime) {
                    earliestStartTime = endTime;
                }
                if (!e.getStartNode().processor.equals(p)) {
                    earliestStartTime = earliestStartTime + e.getWeight();
                }
            }
        }

        return earliestStartTime;
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
     * Returns the name of this node.
     * @return
     */
    public String getName() {
        return name;
    }

}
