package scheduling;

import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by olive on 29/07/2018.
 */
public class SolutionTree {

    private List<Processor> processorList;
    private TaskGraph graph;

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
    	/*List<TaskNode> initialNodes = graph.getAvailableNodes();
    	
    	//Adds initial nodes with no incoming edges to processor
    	int minimialInitialNodeweight = 0;
    	TaskNode initialNode = initialNodes.get(0);
    	
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
    	
    	TaskNode smallestTaskNode = null;
    	
    	// 
    	for (TaskEdge edge : initialNode.getOutgoingEdges()) {
    		
    		TaskNode childNode = edge.getEndNode();
    		int childweight = childNode.getWeight();
    		
    		if (smallestTaskNode.getWeight() < childweight) {
    			smallestTaskNode = childNode;
    		}
    	}
    	*/
    }
    

}
