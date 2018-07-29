package graph;

public class Edge {
    Node startNode;
    Node endNode;
    int weight;

    public Edge(Node startNode, Node endNode, int weight) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.weight= weight;
    }
}
