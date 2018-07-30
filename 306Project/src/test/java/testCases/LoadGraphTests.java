package testCases;


import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;

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

	private static String filePath = "src/main/resources/DotFiles/Nodes_7_OutTree.dot";
	private static Graph graphStreamGraph;
	
	@Before
	public void loadGraphStreamGraph() {
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
	
	@Test
	public void testGraphLoader() {
    	
		GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load(filePath);
		
		HashSet<TaskNode> nodes = graph.getNodes();	
		for (TaskNode taskNode : nodes) {
			boolean nodesTrue = false;
			boolean nodeWeight = false;
		
			for (Node graphStreamNode : graphStreamGraph) {
				if (graphStreamNode.toString().equals(taskNode.getName())) {
					nodesTrue = true;		
				}
				
				
				double nod =  Double.parseDouble(graphStreamNode.getAttribute("Weight").toString());
				int nodeWeightInt = (int) nod;
				if (taskNode.getWeight() == nodeWeightInt ) {
					nodeWeight = true;
				}
			}			
			assertTrue(nodesTrue);
			assertTrue(nodeWeight);
		}
		
		HashSet<TaskEdge> edges = graph.getEdges();

		for (TaskEdge taskEdge : edges) {			
			boolean sourceNodeFromEdges = false;
			boolean targetNodeFromEdges = false;
			boolean taskEdgeWeight = false;
			
			for (Edge edge : graphStreamGraph.getEdgeSet()) {
				Node source = edge.getSourceNode();
				Node target = edge.getTargetNode();
				
				if (source.toString().equals(taskEdge.getStartNode().getName())) {
					sourceNodeFromEdges = true;
				}
				
				if (target.toString().equals(taskEdge.getEndNode().getName())) {
					targetNodeFromEdges = true;
				}
				
				double edgeWeight = Double.parseDouble(edge.getAttribute("Weight").toString());
				int edgeWeightInt = (int) edgeWeight;
				if (edgeWeightInt == taskEdge.getWeight()) {
					taskEdgeWeight = true;
				}
				
			}
			assertTrue(sourceNodeFromEdges);
			assertTrue(targetNodeFromEdges);
			assertTrue(taskEdgeWeight);
			
		}

		
	}
}
