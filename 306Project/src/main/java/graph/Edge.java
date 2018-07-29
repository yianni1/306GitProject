package graph;

/**
 * Created by Dweep on 29/7/2018
 */
public class Edge {
    private Node startNode;
    private Node endNode;
    private int weight;

    /**
     * Creates an edge with a start and end node. Attaches references to the nodes
     * @param startNode
     * @param endNode
     * @param weight
     */
    public Edge(Node startNode, Node endNode, int weight) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.weight= weight;

        startNode.addChild(endNode);
        endNode.addChild(startNode);
    }
}
