package testCases;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
import scheduling.SchedulerI;

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
		//greedySchedule("src/main/resources/DotFiles/Test1.dot");
	}

	private void greedySchedule(String filePath) {

		GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load(filePath);

		GreedyScheduler schedule = new GreedyScheduler(graph, processorNum);
		Schedule solution = schedule.createSchedule();

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
		TaskGraph graph = loader.load("src/main/resources/DotFiles/CustomTest.dot");

		GreedyScheduler schedule = new GreedyScheduler();
		Schedule solution = schedule.createSchedule(graph, 2);
		
		
		
		Output.createOutput(solution.getProcessors(), graph, "greedySolution");

		Schedule correctSolution = new Schedule(2, graph);
		
		List<Processor> processes = correctSolution.getProcessors();
		
		Processor p1 = processes.get(0);
		Processor p2 = processes.get(1);
		
		p1.addTask(new TaskNode(5, "g"), 0);
		p2.addTask(new TaskNode(6, "a"), 0);
		p1.addTask(new TaskNode(7, "b"), 5);
		p1.addTask(new TaskNode(2, "c"), 12);
		p2.addTask(new TaskNode(3, "d"), 14);
		p2.addTask(new TaskNode(8, "f"), 17);
		p2.addTask(new TaskNode(10, "e"), 25);

		Output.createOutput(correctSolution.getProcessors(), graph, "CustomTestAnswer");

		TaskGraph g = loader.load("src/main/resources/DotFiles/CustomTestAnswer-output.dot");
		TaskGraph g1 = loader.load("src/main/resources/DotFiles/greedySolution-output.dot");
		
		assertEquals(g, g1);*/
	}

	@Test
	public void testTest1Dot() throws IOException {
		/*GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load("src/main/resources/DotFiles/Test1.dot");

		GreedyScheduler schedule = new GreedyScheduler(graph, 2);
		Schedule solution = schedule.createSchedule();
		
		Output.createOutput(solution.getProcessors(), graph, "greedySolution");
		
		
		
		Schedule correctSolution = new Schedule(2, graph);
		List<Processor> processes = correctSolution.getProcessors();
		
		Processor p1 = processes.get(0);
		Processor p2 = processes.get(1);
		
		p1.addTask(new TaskNode(3, "a"), 0);
		p1.addTask(new TaskNode(4, "b"), 3);
		p2.addTask(new TaskNode(6, "c"), 4);
		p1.addTask(new TaskNode(3, "d"), 10);

		Output.createOutput(processes, graph, "CorrectSolution");*/
		
		// boolean same = compareTextFiles("src/main/resources/DotFiles/greedySolution-output.dot", "src/main/resources/DotFiles/CorrectSolution-output.dot");
	  //   assertTrue(same);
	}
	
	@Test
	public void testTripleProcessorDot() throws IOException {
		GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load("src/main/resources/DotFiles/TripleProcessor.dot");
		
		SchedulerI schedule = new GreedyScheduler(graph, 3);
		Schedule solution = schedule.createSchedule();
		
		Output.createOutput(solution.getProcessors(), graph, "greedySolutionTripleProcessor");
		
		
		
		Schedule correctSolution1 = new Schedule(3, graph);
		List<Processor> processes1 = correctSolution1.getProcessors();
		
		Processor p1 = processes1.get(0);
		Processor p2 = processes1.get(1);
		Processor p3 = processes1.get(2);
		
		p1.addTask(new TaskNode(2, "d"), 0);
		p2.addTask(new TaskNode(5, "a"), 0);
		p3.addTask(new TaskNode(8, "g"), 0);
		p2.addTask(new TaskNode(4, "b"), 5);
		p3.addTask(new TaskNode(6, "c"), 8);
		p3.addTask(new TaskNode(7, "f"), 14);
		p2.addTask(new TaskNode(4, "e"), 19);
		
		Output.createOutput(processes1, graph, "CorrectTripleProcessor");
		
		
		Schedule correctSolution2 = new Schedule(3, graph);
		List<Processor> processes2 = correctSolution2.getProcessors();
		
		Processor twop1 = processes2.get(0);
		Processor twop2 = processes2.get(1);
		Processor twop3 = processes2.get(2);
		
		twop1.addTask(new TaskNode(2, "d"), 0);
		twop2.addTask(new TaskNode(5, "a"), 0);
		twop3.addTask(new TaskNode(8, "g"), 0);
		twop2.addTask(new TaskNode(4, "b"), 5);
		twop3.addTask(new TaskNode(6, "c"), 8);
		twop2.addTask(new TaskNode(7, "f"), 24234234);
		twop3.addTask(new TaskNode(4, "e"), 14);
		
		Output.createOutput(processes2, graph, "CorrectTripleProcessor");
		
		
		///boolean same = compareTextFiles("src/main/resources/DotFiles/CorrectTripleProcessor-output.dot", "src/main/resources/DotFiles/greedySolutionTripleProcessor-output.dot");
	   // assertTrue(same);
	}
	
	
	/**
     * Compares 2 text files
     * @param file1
     * @param file2
     * @return true if they're the same, false if not the same.
     * @throws IOException
     */
    public boolean compareTextFiles(String file1, String file2) throws IOException {
        boolean areEqual = true;

        int lineNum1 = 1;
        int lineNum2 = 2;

        BufferedReader reader1 = new BufferedReader(new FileReader(file1));
        String line1 = reader1.readLine();
        while (line1 != null) {
            //System.out.println("TextA: Reading line " + lineNum1);
            BufferedReader reader2 = new BufferedReader(new FileReader(file2));
            String line2 = reader2.readLine();
            lineNum2 = 1;
            while (line2 != null) {
             //   System.out.println("TextB: Reading line " + lineNum2);
                if(!line1.equals(line2)) {
                    line2 = reader2.readLine();
                    lineNum2++;
                    if(line2 == null) {
                        areEqual = false;
                        System.out.println("Line " + lineNum1 + " does not appear in the second file.");
                        break;
                    }
                } else {
                    System.out.println("Line " + lineNum1 + " appears in line " + lineNum2 + " of TextB.");
                    break;
                }
            }
            reader2.close();
            line1 = reader1.readLine();
            lineNum1++;

        }
        reader1.close();

        if(areEqual) {
            System.out.println("Two files have same content.");
            return true;
        }
        else {
            return false;
        }
    }
	
	
}
