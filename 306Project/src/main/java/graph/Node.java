package graph;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private int weight;
    private char name;
    private boolean marked;

    private List<Node> parents = new ArrayList<Node>();
    private List<Node> children = new ArrayList<Node>();
    //Edge[] edges;

    /**
     * Constructor takes in the weight and the name of the node
     * @param weight
     * @param name
     */
    public Node(int weight, char name) {
        this.name = name;
        this.weight = weight;
        this.marked = false;
    }

    /**
     * Adds a node to the parent
     * @param node the node pointing to this node
     */
    void addParent(Node node) {
        this.parents.add(node);
    }

    /**
     * Adds a node to children
     * @param node the node this points to
     */
    void addChild(Node node) {
        this.children.add(node);
    }
}
