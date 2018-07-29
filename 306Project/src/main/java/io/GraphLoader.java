package io;

import java.io.IOException;
import java.util.HashSet;

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
	public Graph load(String filePath) {
		
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
		
		//convertGraph(graph);
		
		return graph;
	}
	/*
	private void convertGraph(Graph graph) {
		
		TaskGraph taskGraph = new TaskGraph();
		
		for (Node node : graph) {
			
			TaskNode taskNode = createTaskNode(node);
			
			HashSet<TaskEdge> edges = new HashSet<TaskEdge>();
			for (Edge edge : node) {	
				TaskEdge taskEdge = createTaskEdge(edge, taskNode);
			}
			//taskGraph.addNode(taskNode);			
		}
		
		
	}
	
	private TaskEdge createTaskEdge(Edge edge, TaskNode taskNode) {
		int edgeWeight = (int) edge.getAttribute("Weight");
		
		Node source = edge.getSourceNode();
		Node target = edge.getTargetNode();
		
		TaskNode sourceTaskNode = createTaskNode(source);
		TaskNode targetTaskNode = createTaskNode(target);
		
		TaskEdge taskEdge = new TaskEdge(sourceTaskNode, targetTaskNode, edgeWeight);
		
		return taskEdge;
	}

	private TaskNode createTaskNode(Node node) {
		int nodeWeight = (int) node.getAttribute("Weight");
		String nodeName = node.toString();
	
		TaskNode taskNode = new TaskNode(nodeWeight, nodeName);
		return taskNode;
	}
	*/

}
