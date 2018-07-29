package graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Node {
    int weight;
    char name;
    boolean marked;

    HashMap<Node,Boolean> parents;
    HashMap<Node,Boolean> children;
    //Edge[] edges;

    public Node(int weight, char name) {
        this.name = name;
        this.weight = weight;
        this.marked = false;
    }
    /*
    public Node getUnvisitedChildNode() {
        if (this.children.containsValue(true)) {
            for (Map.Entry<Node,Boolean> entry : this.children.entrySet()) {
                if () {
                    return ;
                }
            }
        }
    }
    */
}
