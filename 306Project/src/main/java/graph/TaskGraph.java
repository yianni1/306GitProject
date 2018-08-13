package graph;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by olive on 29/07/2018.
 * This is the TaskGraph class which stores an Hashset of all the available edges and Nodes and there respective connections
 * to create the representation of the graph to be delievered to the Scheduler and algo components of the project.
 */
public class TaskGraph implements Serializable, Cloneable {

    private HashSet<TaskEdge> edges; // The edges in the graph
    private HashSet<TaskNode> nodes; // The nodes in the graph
    private String title;


    @Override
    public TaskGraph clone() throws CloneNotSupportedException {
    	
    	TaskGraph graph = new TaskGraph(this.title);
    	  
    	for (TaskNode node : nodes) {
    		graph.addNode(node);
    	}
    	for (TaskEdge edge : edges) {
    		graph.addEdge(edge);
    	}
    	
        return graph;
    }

    

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

    /**
     * Remove a node
     * @param node
     */
    public void removeNode(TaskNode node) {
        this.nodes.remove(node);
    }
    
    /**
     * Remove an edge
     * @param node
     */
    public void removeEdge(TaskEdge edge) {
        this.edges.remove(edge);
    }
    
    /**
     * Returns a Hashset of all the available nodes
     * @return
     */
    public HashSet<TaskNode> getNodes() {
        return this.nodes;
    }

    /**
     * Returns a Hashset of all the avilable edges
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
