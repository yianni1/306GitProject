package graph;

import scheduling.Processor;

import java.util.HashSet;

/**
 * created by Dweep on 29/7/2018
 */
public class TaskNode {
    private int weight;
    private String name;
    private boolean scheduled;
    private int startTime;

    private HashSet<TaskEdge> incomingEdges;
    private HashSet<TaskEdge> outgoingEdges;

    public TaskNode(int weight, String name) {
        this.name = name;
        this.weight = weight;
        this.scheduled = false;
        this.startTime = -1;
        incomingEdges = new HashSet<TaskEdge>();
        outgoingEdges = new HashSet<TaskEdge>();
    }

    public boolean schedule(int startTime, Processor processor) {
        if (!this.isSchedulable()) {
            return false;
        }
        this.startTime = processor.getCost();

        this.scheduled = true;
        processor.addTask(this, this.weight);
        return true;
    }

    public int getEndTime() {
        return this.startTime + this.weight;
    }
    
    public int getStartTime() {
    	return this.startTime;
    }
    
    /**
     * This should only be run if the task is schedulable.
     * It should return the earliest schedulable time.
     * TODO: IMCOMPLETE
     *
     * @return
     */
    public int getEarliestSchedulbleTime() {
        int latestEndTime = -1;
        if (this.isSchedulable()) {
            for (TaskEdge e : this.getIncomingEdges()) {
                int endTime = e.getStartNode().getEndTime();
                if (endTime > latestEndTime) {
                    latestEndTime = endTime;
                }
                e.getWeight();
            }
        }

        return latestEndTime;
    }


    public void addIncomingEdge(TaskEdge edge) {
        incomingEdges.add(edge);
    }

    public void addOutgoingEdge(TaskEdge edge) {
        outgoingEdges.add(edge);
    }

    public HashSet<TaskEdge> getIncomingEdges() {
        return incomingEdges;
    }

    public HashSet<TaskEdge> getOutgoingEdges() {
        return outgoingEdges;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isScheduled() {
        return scheduled;
    }
    
    public void schedule(int time) {
    	scheduled = true;
    	this.startTime = time;
    }

    public boolean isSchedulable() {
        for (TaskEdge e : incomingEdges) {
            if (!e.getStartNode().isScheduled()) {
                return false;
            }
        }
        return true;
    }

    public String getName() {
        return name;
    }

}
