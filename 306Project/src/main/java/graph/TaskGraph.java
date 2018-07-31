package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by olive on 29/07/2018.
 */
public class TaskGraph {

    private HashSet<TaskEdge> edges; //The edges of the graph
    private HashSet<TaskNode> nodes;
    private List<TaskNode> availableNodes;
    
    public TaskGraph() {
    	edges = new HashSet<TaskEdge>();
    	nodes = new HashSet<TaskNode>();
    	availableNodes = new ArrayList<TaskNode>();
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
    	 node.setGraph(this); //Nodes maintain a refernce to the graph
    }
    
    public HashSet<TaskNode> getNodes() {
    	return nodes;
    }
    
    public HashSet<TaskEdge> getEdges() {
    	return this.edges;
    }

    /**
     * Indicates that a node has been made available
     * @param node the node that is now avialable
     */
    void nodeMadeAvailable(TaskNode node) {
        availableNodes.add(node);
    }

    /**
     * Indicates that a node is no longer available
     * @param node
     */
    void nodeNoLongerAvailable(TaskNode node) {
        availableNodes.remove(node);
    }

    /**
     * Returns all the available nodes in the graph
     * @return
     */
    public List<TaskNode> getAvailableNodes() {
        return availableNodes;
    }
    
}
