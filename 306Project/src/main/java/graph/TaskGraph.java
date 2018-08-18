package graph;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by olive on 29/07/2018.
 * This is the TaskGraph class which stores an HashSet of all the available edges and Nodes and their respective connections
 * to create the representation of the graph. This representation is to be delivered to the Scheduler and also components
 * of the project.
 */
public class TaskGraph implements Serializable {

    private HashSet<TaskEdge> edges; // The edges in the graph
    private HashSet<TaskNode> nodes; // The nodes in the graph
    private String title; //The title of the graph

    /**
     * Constructor takes in a title, and creates an empty TaskGraph
     * @param title the title of the graph
     */
    public TaskGraph(String title) {
        edges = new HashSet<>();
        nodes = new HashSet<>();
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

    /**
     * Returns a Hashset of all the available nodes
     * @return
     */
    public HashSet<TaskNode> getNodes() {
        return this.nodes;
    }

    /**
     * Returns a Hashset of all the available edges
     * @return
     */
    public HashSet<TaskEdge> getEdges() {
        return this.edges;
    }

    /**
     * Gets the title of the graph
     * @return
     */
    public String getTitle() {
        return title;
    }

}
