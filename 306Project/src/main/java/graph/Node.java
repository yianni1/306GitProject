package graph;

public class Node {
    int weight;
    char name;
    boolean marked;

    Node[] parent;
    Node[] child;
    //Edge[] edges;

    public Node(int weight, char name) {
        this.name = name;
        this.weight = weight;
        this.marked = false;
    }
}
