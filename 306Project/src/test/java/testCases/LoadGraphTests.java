package testCases;


import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import org.junit.Before;
import org.junit.Test;

import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;
import io.GraphLoader;

public class LoadGraphTests {

//	private static String filePath = "src/main/resources/DotFiles/Nodes_7_OutTree.dot";
	private static Graph graphStreamGraph;
	private static List<String> filePaths;
	
//	/**
//	 * Before method to load the dot file as a GraphStream Graph
//	 */
//	@Before
//	public void loadGraphStreamGraph() {
//
//	}

	@Before
	public void initialisePaths() {
		filePaths = new ArrayList<String>();
		filePaths.addAll(Arrays.asList("src/main/resources/DotFiles/Nodes_7_OutTree.dot", "src/main/resources/DotFiles/Test1.dot",
				"src/main/resources/DotFiles/TestTwoParents.dot"));
	}

	
	/**
	 * Tests that GraphLoader correctly loads the GraphStreamGraph into TaskGraph
	 */
	@Test
	public void testGraphLoader() {
		for (String filePath : filePaths) {
			System.out.println(filePath);
			loadGraphStreamGraph(filePath);
			testGraphLoader(filePath);
		}
	}

	/**
	 * Tests graph loader for a particular graph
	 * @param filePath
	 */
	private void testGraphLoader(String filePath) {
		GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load(filePath);

		//This block checks the node names and weights are the same
		HashSet<TaskNode> nodes = graph.getNodes();
		for (TaskNode taskNode : nodes) {
			boolean nodesTrue = false;
			boolean nodeWeight = false;

			for (Node graphStreamNode : graphStreamGraph) {
				//If GraphStream Graph node name equals taskNode name, node is correct
				if (graphStreamNode.toString().equals(taskNode.getName())) {
					nodesTrue = true;
				}

				//If GraphStream weight is equal to TaskNode weight, nodeWeight is correct.
				double nod =  Double.parseDouble(graphStreamNode.getAttribute("Weight").toString());
				int nodeWeightInt = (int) nod;
				if (taskNode.getWeight() == nodeWeightInt) {
					nodeWeight = true;
				}
			}
			//node names and weights should be true for all iterations
			assertTrue(nodesTrue);
			assertTrue(nodeWeight);
		}

		HashSet<TaskEdge> edges = graph.getEdges();

		//This block checks the edges source and target nodes match the GraphStream graph
		//Also checks weights
		for (TaskEdge taskEdge : edges) {
			boolean sourceNodeFromEdges = false;
			boolean targetNodeFromEdges = false;
			boolean taskEdgeWeight = false;

			for (Edge edge : graphStreamGraph.getEdgeSet()) {
				Node source = edge.getSourceNode();
				Node target = edge.getTargetNode();

				//If sourceNode name of GraphStream edge equals name of taskEdge start node,
				//TaskGraph source node of TaskEdge is correct
				if (source.toString().equals(taskEdge.getStartNode().getName())) {
					sourceNodeFromEdges = true;
				}

				//If targetNode name of GraphStream edge equals name of taskEdge end node,
				//TaskGraph target node of TaskEdge is correct
				if (target.toString().equals(taskEdge.getEndNode().getName())) {
					targetNodeFromEdges = true;
				}

				//Ensures weights are the same for TaskEdge and edge
				double edgeWeight = Double.parseDouble(edge.getAttribute("Weight").toString());
				int edgeWeightInt = (int) edgeWeight;
				if (edgeWeightInt == taskEdge.getWeight()) {
					taskEdgeWeight = true;
				}

			}
			//edge names and weights should be true for all iterations
			assertTrue(sourceNodeFromEdges);
			assertTrue(targetNodeFromEdges);
			assertTrue(taskEdgeWeight);

		}
	}


	private void loadGraphStreamGraph(String filePath) {
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
		graphStreamGraph = graph;
	}



}
