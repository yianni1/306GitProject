package graph;

import java.util.HashSet;

/**
 * created by Dweep on 29/7/2018
 */
public class TaskNode {
    private int weight;
    private char name;
    private boolean visited;

    private HashSet<TaskEdge> incomingEdges;
    private HashSet<TaskEdge> outgoingEdges;

    public TaskNode(int weight, char name) {
        this.name = name;
        this.weight = weight;
        this.visited = false;
        incomingEdges = new HashSet<TaskEdge>();
        outgoingEdges = new HashSet<TaskEdge>();
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

    public boolean visited() {
        return visited;
    }

    public char getName() {
        return name;
    }


    /*public TaskNode getUnvisitedChildNode() {
        if (this.children.containsValue(true)) {
            for (Map.Entry<TaskNode,Boolean> entry : this.children.entrySet()) {
                if () {
                    return ;
                }
            }
        }
    }
    */

}
