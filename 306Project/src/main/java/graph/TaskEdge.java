package graph;

/**
 * Created by Dweep on 29/7/2018
 */
public class TaskEdge {
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
}
