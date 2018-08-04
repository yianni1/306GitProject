package graph;

import java.io.Serializable;

/**
 * Created by Dweep on 29/7/2018
 */
public class TaskEdge implements Serializable {
    private TaskNode startNode;
    private TaskNode endNode;
    private int weight;

    /**
     * Creates an edge with a start and end node. Attaches references to the nodes
     * @param startNode
     * @param endNode
     * @param weight
     */
    public TaskEdge(TaskNode startNode, TaskNode endNode, int weight) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.weight= weight;
    }

    public TaskNode getStartNode() {
        return startNode;
    }

    public TaskNode getEndNode() {
        return endNode;
    }

    public int getWeight() {
        return weight;
    }
    
    public void setStartNode(TaskNode startNode) {
    	this.startNode = startNode;
    }
    
    public void setEndNode(TaskNode endNode) {
    	this.endNode = endNode;
    }
}
