package scheduling;

import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by olive on 29/07/2018.
 */
public class SolutionTree {

    private List<Processor> processorList;
    private TaskGraph graph;
    
    private List<TaskNode> initialNodes;

    /**
     * Constructor builds a solution tree, with all the solutions
     * @param startingNodes
     * @param processorNumber
     */
    public SolutionTree(TaskGraph graph, int processors) {
    	
    	processorList = new ArrayList<Processor>();

        for (int i = 0; i < processors; i++) {
        	processorList.add(new Processor(i));
        }
    	this.graph = graph;
    	
    }
    
    /**
     * Implementation of the graph scheduling
     */
    public void doSchedule() {
    	/*initialNodes = graph.getAvailableNodes();
    	
    	
    	int minimialInitialNodeweight = 0;
    	TaskNode initialNode = initialNodes.get(0);
    	
    	//Adds initial nodes with no incoming edges to processor
    	for (TaskNode taskNode : initialNodes) {
    		
    		// Gets node with minimal weight and keeps it, to be scheduled on processor
    		int weight = taskNode.getWeight();
    		if (minimialInitialNodeweight < weight) { 
    			minimialInitialNodeweight = weight;
    			initialNode = taskNode;
    		}
    	}
    
    	//Schedules minimal node with minimal weight first
    	processorList.get(0).addTask(initialNode);
    	initialNodes.remove(initialNode);
    	
    	
    	TaskNode smallestTaskNode = null;
    	int smallestTaskNodeWeight = 0;
    	
    	for (TaskNode )
    	// Checks child nodes of initial node
    	for (TaskEdge edge : initialNode.getOutgoingEdges()) {
    		
    		TaskNode currentchildNode = edge.getEndNode();
    		int childweight = currentchildNode.getWeight();
    		
    		
    		   		
    		//Checks the weight of the current child and sees if it is less than current 
    		//to be scheduled node
    		if (smallestTaskNodeWeight < childweight) {
    			
    			HashSet<TaskEdge> parentEdges = smallestTaskNode.getIncomingEdges();
    			
    			boolean parentsScheduled = true;
    			//Checks if current child's parent is visted
    			for (TaskEdge parentEdge : parentEdges) {
    				if (parentEdge.getStartNode().equals(currentchildNode)) {
    					
    					if (!parentEdge.getStartNode().isScheduled()) {
    						parentsScheduled = false;
    						break;
    					}
    				}
    			}

    			if (parentsScheduled) {
	    			smallestTaskNode = currentchildNode;
	    			smallestTaskNodeWeight = childweight;
    			}
    			else {
    				newInitialNode(currentchildNode);
    			}
    		}
    		
    	}
    	
    	*/
    	
    }
    
    /**
     * Schedules another initial node
     */
    private void newInitialNode(TaskNode smallestTaskNode) {
    	HashSet<TaskEdge> edges = smallestTaskNode.getIncomingEdges();
    	
    	boolean hasAllParentsScheduled = true;
    	for (TaskEdge edge : edges) {
    		TaskNode startNode = edge.getStartNode();
    		
    		if (!startNode.isScheduled()) {
    			hasAllParentsScheduled = false;
    			break;
    		}
    	}
    	
    	
    	//TEST WITH OLivers INPUT
    //	initialNodes.remove(index)

    }
    

}
