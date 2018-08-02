package testCases;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.graphstream.graph.Graph;
import org.junit.Before;
import org.junit.Test;

import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;
import io.GraphLoader;
import scheduling.GreedySchedule;
import scheduling.Processor;
import scheduling.Schedule;

public class TestGreedyAlgorithum {

	
	private static Graph graphStreamGraph;
	private static List<String> filePaths;
	private static int processorNum;

	@Before
	public void initialisePaths() {
		
		processorNum = 2;
		
		filePaths = new ArrayList<String>();
		filePaths.addAll(Arrays.asList("src/main/resources/DotFiles/Nodes_7_OutTree.dot", "src/main/resources/DotFiles/Test1.dot",
				"src/main/resources/DotFiles/TestTwoParents.dot", "src/main/resources/DotFiles/Nodes_10_Random.dot", 
				"src/main/resources/DotFiles/Nodes_9_SeriesParallel.dot", "src/main/resources/DotFiles/Nodes_11_OutTree.dot",
				"src/main/resources/DotFiles/Nodes_8_Random.dot"));
	}
	
	@Test 
	public void testGreedySchedule() {
		for (String filePath : filePaths) {
			greedySchedule(filePath);
		}
	}
	
	private void greedySchedule(String filePath) {
		
		GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load(filePath);
		
		GreedySchedule schedule = new GreedySchedule(graph, processorNum);
		Schedule solution = schedule.doSchedule();
		
		
		for (Processor processor : solution.getProcessors()) {
			int totalWeight = 0;
			
			for (TaskNode node : processor.getTasks()) {
				
				for (TaskEdge edge : node.getIncomingEdges()) {
					TaskNode parentNode = edge.getStartNode();
					int parentStart = parentNode.getStartTime();
					int nodeStart = node.getStartTime();
					
					System.out.println(parentNode.getName() + " " + parentStart + " " + node.getName() + " " + nodeStart);
					//Checks that the current node is scheduled later than its parents
					assertTrue(parentStart <= nodeStart);
					
				}
			}
			
			//Checks each node is only schedule once, i.e. no duplicate nodes in any processor
			HashSet<TaskNode> set = new HashSet<TaskNode>(processor.getTasks());
			if(set.size() < processor.getTasks().size()){
			   fail();
			}
			
		}
	}

}
