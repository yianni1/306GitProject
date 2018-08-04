package graph;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by olive on 29/07/2018.
 */
public class TaskGraph implements Serializable {

    private HashSet<TaskEdge> edges; // The edges in the graph
    private HashSet<TaskNode> nodes; // The nodes in the graph
    private String title;


    public TaskGraph(String title) {
        edges = new HashSet<TaskEdge>();
        nodes = new HashSet<TaskNode>();
        this.title = title;
    }

    /**
     * Adds an edge
     * @param edge
     */
    public void addEdge(TaskEdge edge) {
        this.edges.add(edge);
    }

    /**
     * Adds a node
     * @param node
     */
    public void addNode(TaskNode node) {
        this.nodes.add(node);
    }

    public HashSet<TaskNode> getNodes() {
        return this.nodes;
    }

    public HashSet<TaskEdge> getEdges() {
        return this.edges;
    }

    public String getTitle() {
        return title;
    }

}
