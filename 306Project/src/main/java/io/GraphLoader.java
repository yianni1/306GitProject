package io;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

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
 * Cleaned by Dweep and Oliver
 */
public class GraphLoader {

	/**
	 * The Dotfile is taken in and and loaded as a graph which is then outputted
	 * as a Graphstream graph using the graph stream library
	 *
	 * @param filePath of the Dot file
	 * @return Graph from the Dot file
	 */
	public Graph loadGraph(String filePath) {

		//creates the graph
		Graph graph = new SingleGraph("graph");

		FileSource fs = null;

		// the graph is loaded from the filepath
		try {
			fs = FileSourceFactory.sourceFor(filePath);
			fs.addSink(graph);
			fs.readAll(filePath);
		}
		catch( IOException e) {

			//catches and input and output exception
			e.printStackTrace();

			//validity check
			System.out.println("Please check that the filepath for the Dotfile is correctly inputtted");
		}
		finally {


			fs.removeSink(graph);
		}

		//returns the loaded graph
		return graph;
	}

	/**
	 * load method to load graph from dot file into TaskGraph object
	 * @param filePath to dot file
	 * @return graph representation of dot file
	 */
	public TaskGraph load(String filePath) {

		//Finding the name of the graph
		String graphTitle = null;
		try {

			//seperates based on dot file seperators
			File file = new File(filePath);
			Scanner sc = new Scanner(file);
			graphTitle = sc.nextLine().split("\"")[1];
			sc.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		//Loading the dot file into a GraphStream graph
		Graph graph = loadGraph(filePath);

		//Converting from the GraphStream graph to the taskgraph
		TaskGraph taskGraph = convertGraph(graph, graphTitle);

		return taskGraph;
	}

	/**
	 * convertGraph method to convert GraphStream graph into TaskGraph data Structure
	 * @param graph Taskgraph representation of dot file
	 * @return TaskGraph representation of dot file
	 */
	private TaskGraph convertGraph(Graph graph, String graphTitle) {

		TaskGraph taskGraph = new TaskGraph(graphTitle);

		//Creates TaskNodes for each node in GraphStream graph
		for (Node node : graph) {

			TaskNode taskNode = createTaskNode(node);

			taskGraph.addNode(taskNode);
		}

		//Creates Edges for the TaskGraph according to the GraphSteam input and TaskGraph Nodes
		for (Edge edge : graph.getEdgeSet()) {

			//Gets the nodes of the taskGraph
			HashSet<TaskNode> tNodesSet = taskGraph.getNodes();

			//Gets the source and target nodes the edge of the GraphStream graph is attached too
			Node source = edge.getSourceNode();
			Node target = edge.getTargetNode();


			//Gets the weights for the edge, source node and target node from the GraphStream graph
			double edgeWeight = Double.parseDouble(edge.getAttribute("Weight").toString());
			int edgeWeightInt = (int) edgeWeight;

			double sourceWeight = Double.parseDouble(source.getAttribute("Weight").toString());
			int sourceWeightInt = (int) sourceWeight;

			double targetWeight = Double.parseDouble(target.getAttribute("Weight").toString());
			int targetWeightInt = (int) targetWeight;

			//Creates new task nodes according to the edge for comparison to the Task Nodes created in createTaskNode()
			TaskNode sourceTaskNode = new TaskNode(sourceWeightInt, source.toString());
			TaskNode targetTaskNode = new TaskNode(targetWeightInt, target.toString());

			//Compare the taskNodes created above with the taskNodes in the TaskGraph
			TaskEdge tEdge;
			for (TaskNode tNode : tNodesSet) {
				if (sourceTaskNode.getName().equals(tNode.getName())) {
					for (TaskNode tNodeA : tNodesSet) {
						if (targetTaskNode.getName().equals(tNodeA.getName())) {

							//When source node and target node have the same name as the corresponding TaskGraph nodes
							//Create the TaskEdge according to the TaskGraph nodes and add the TaskEdge reference to the taskNodes
							tEdge = new TaskEdge(tNode, tNodeA, edgeWeightInt);
							tNode.addOutgoingEdge(tEdge);
							tNodeA.addIncomingEdge(tEdge);
							taskGraph.addEdge(tEdge);
						}
					}
				}
			}


		}

		return taskGraph;
	}

	/**
	 * createTaskNode method to create a TaskNode from the corresponding GraphStream node
	 * @param node GraphStream node
	 * @return TaskGraph node
	 */
	private TaskNode createTaskNode(Node node) {

		//Getting the "weight" attribute from the .dot file
		double nodeWeight =  Double.parseDouble(node.getAttribute("Weight").toString());
		int nodeWeightInt = (int) nodeWeight;

		//Setting the name of the node
		String nodeName = node.toString();

		//Creates the TaskNode according to its weight and name
		TaskNode taskNode = new TaskNode(nodeWeightInt, nodeName);

		return taskNode;
	}

}
