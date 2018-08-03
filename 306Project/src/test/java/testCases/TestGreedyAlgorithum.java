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
import io.Output;
import scheduling.GreedyScheduler;
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
		//for (String filePath : filePaths) {
		//	greedySchedule(filePath);
		//}
		greedySchedule("src/main/resources/DotFiles/Test1.dot");
	}

	private void greedySchedule(String filePath) {

		GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load(filePath);

		GreedyScheduler schedule = new GreedyScheduler();
		Schedule solution = schedule.createSchedule(graph, processorNum);

		int nodeNum = 0;
		for (Processor processor : solution.getProcessors()) {
			int totalWeight = 0;

			for (TaskNode node : processor.getTasks()) {

				for (TaskEdge edge : node.getIncomingEdges()) {
					TaskNode parentNode = edge.getStartNode();
					int parentStart = parentNode.getEndTime();
					int nodeStart = node.getEndTime();

					//System.out.println(parentNode.getName() + " " + parentStart + " " + node.getName() + " " + nodeStart);
					//Checks that the current node is scheduled later than its parents
					assertTrue(parentStart <= nodeStart);

				}
				nodeNum++;
			}

			//Checks each node is only schedule once, i.e. no duplicate nodes in any processor
			HashSet<TaskNode> set = new HashSet<TaskNode>(processor.getTasks());
			if(set.size() < processor.getTasks().size()){
			   fail();
			}

		}

	}
	
	@Test
	public void testCustomTestDot() {
		/*GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load("src/main/resources/DotFiles/Test1.dot");

		GreedyScheduler schedule = new GreedyScheduler();
		Schedule solution = schedule.createSchedule(graph, 2);
		
		Schedule correctSolution = new Schedule(2, graph);
		
		List<Processor> processes = correctSolution.getProcessors();
		
		Processor p1 = new Processor(0);
		Processor p2 = new Processor(1);
		
		p1.addTask(new TaskNode(5, "g"), 0);
		p2.addTask(new TaskNode(6, "a"), 0);
		p1.addTask(new TaskNode(7, "b"), 5);
		p1.addTask(new TaskNode(2, "c"), 12);
		p2.addTask(new TaskNode(3, "d"), 14);
		p2.addTask(new TaskNode(8, "f"), 17);
		p2.addTask(new TaskNode(10, "e"), 25);
		

		Output.createOutput(solution.getProcessors(), graph, "hello");
		
		for (Processor processor : solution.getProcessors()) {
			if (processor.getID() == p1.getID()) {
				assertEquals(processor.getTasks(), p1.getTasks());
			}
		}*/
	}

}
