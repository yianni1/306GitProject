package io;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;

import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;


/**
 * Created by yianni on 28/07/2018.
 */
public class GraphLoader {


	/**
	 * load method to load graph from dot file into TaskGraph object
	 * @param filepath to dot file
	 * @return graph representation of dot file
	 */
	public TaskGraph load(String filePath) {

		Graph graph = new SingleGraph("graph"); // Creates graph
		FileSource fs = null;		

		// Loads graph from filepath
		try {
			fs = FileSourceFactory.sourceFor(filePath); 

			fs.addSink(graph);

			fs.readAll(filePath);

		} catch( IOException e) {

		} finally {
			fs.removeSink(graph);
		}

		TaskGraph taskGraph = convertGraph(graph);
		
		return taskGraph;
	}
	
	
	private TaskGraph convertGraph(Graph graph) {

		TaskGraph taskGraph = new TaskGraph();

		//Creates TaskNodes for each node in GraphStream graph
		for (Node node : graph) {

			TaskNode taskNode = createTaskNode(node);

			taskGraph.addNode(taskNode);
		}

		//Creates Edges for 
		for (Edge edge : graph.getEdgeSet()) {
			HashSet<TaskNode> tNodesSet = taskGraph.getNodes();

			Node source = edge.getSourceNode();
			Node target = edge.getTargetNode();
			
			double edgeWeight = (double) edge.getAttribute("Weight");
			int edgeWeightInt = (int) edgeWeight;
			
			double sourceWeight = (double) source.getAttribute("Weight");
			int sourceWeightInt = (int) sourceWeight;
			
			double targetWeight = (double) target.getAttribute("Weight");
			int targetWeightInt = (int) targetWeight;
			
			TaskNode sourceTaskNode = new TaskNode(sourceWeightInt, source.toString());
			TaskNode targetTaskNode = new TaskNode(targetWeightInt, target.toString());
			
			TaskEdge tEdge = null;
			for (TaskNode tNode : tNodesSet) {
				if (sourceTaskNode.getName().equals(tNode.getName())) {
					for (TaskNode tNodeA : tNodesSet) {
						if (targetTaskNode.getName().equals(tNodeA.getName())) {
							 tEdge = new TaskEdge(tNode, tNodeA, edgeWeightInt);
							 tNode.addOutgoingEdge(tEdge);
							 tNodeA.addIncomingEdge(tEdge);
						}
					}
				}		
			}
			

		}

/*
		HashSet<TaskNode> nde = taskGraph.getNodes();
		for (TaskNode n : nde) {
			HashSet<TaskEdge> tEdges = n.getIncomingEdges();
			System.out.println("Node " + n.getName());
			for (TaskEdge e : tEdges) {
				System.out.println("Edge " + e.getWeight());
			}
		}*/
		return taskGraph;
	}



	private TaskNode createTaskNode(Node node) {
		double nodeWeight = (double) node.getAttribute("Weight");
		int nodeWeightInt = (int) nodeWeight;

		String nodeName = node.toString();

		TaskNode taskNode = new TaskNode(nodeWeightInt, nodeName);

		return taskNode;
	}

}
