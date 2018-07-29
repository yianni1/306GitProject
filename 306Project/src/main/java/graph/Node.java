package graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * created by Dweep on 29/7/2018
 */
public class Node {
    int weight;
    char name;
    boolean marked;

    HashSet<Edge> incomingEdge;
    HashSet<Edge> OutgoingEdge;
    //Edge[] edges;

    public Node(int weight, char name) {
        this.name = name;
        this.weight = weight;
        this.marked = false;
    }

    /*public Node getUnvisitedChildNode() {
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
