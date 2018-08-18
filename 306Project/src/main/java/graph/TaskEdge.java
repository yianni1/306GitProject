package graph;

import java.io.Serializable;

/**
 * Created by Dweep on 29/7/2018
 * This is the class which creates a the Edge data structure which stores the startNode , EndNode and the weight of the
 * Edge which will later be delivered into the scheduler class and the algorithim iteration.
 */
public class TaskEdge implements Serializable {

    private TaskNode startNode; //The node that makrs the entry point of the edge
    private TaskNode endNode; //The node that marks the end of the edge
    private int weight; //The value of the weight of the edge

    /**
     * Creates an edge with a start and end node. Attaches references to the nodes
     * @param startNode the starting node
     * @param endNode the end node
     * @param weight the value of the weight of the edge
     */
    public TaskEdge(TaskNode startNode, TaskNode endNode, int weight) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.weight= weight;
    }

    /**
     * Returns the startnode of the edge
     * @return the starting node
     */
    public TaskNode getStartNode() {
        return startNode;
    }

    /**
     * Returns the end node of the edge
     * @return the end node of the edge
     */
    public TaskNode getEndNode() {
        return endNode;
    }

    /**
     * Returns the weight of the edge
     * @return
     */
    public int getWeight() {
        return weight;
    }

}
