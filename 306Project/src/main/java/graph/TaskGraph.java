package graph;

import java.util.HashSet;

/**
 * Created by olive on 29/07/2018.
 */
public class TaskGraph {

    private HashSet<TaskEdge> edges; //The edges of the graph
    private HashSet<TaskNode> nodes;
    
    public TaskGraph() {
    	edges = new HashSet<TaskEdge>();
    	nodes = new HashSet<TaskNode>(); 
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
    	return nodes;
    }
    
    public HashSet<TaskEdge> getEdges() {
    	return this.edges;
    }
    
    
    
}
