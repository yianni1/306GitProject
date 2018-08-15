package testCases;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Test;

import graph.TaskGraph;
import graph.TaskNode;
import io.GraphLoader;
import io.Output;
import main.App;
import scheduling.DFBnBScheduler;
import scheduling.GreedyScheduler;
import scheduling.Processor;
import scheduling.Schedule;
import scheduling.Scheduler;

public class OptimalTestCase extends CompareOutput {

	@Test
	public void test() throws IOException, URISyntaxException {
		GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load("src/main/resources/DotFiles/OptimalTest.dot");

		String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
		File parent = new File(path);
		String parentPath = parent.getParent() + "\\";

		Scheduler schedule = new DFBnBScheduler(graph, 2);
		Schedule solution = schedule.createSchedule();

		Output.createOutput(solution.getProcessors(), graph, parentPath + "optimalSolution.dot");


		Schedule correctSolution = new Schedule(2, graph);
		List<Processor> processes = correctSolution.getProcessors();

		Processor p1 = processes.get(0);
		Processor p2 = processes.get(1);

		p1.addTask(new TaskNode(2, "a"), 0);
		p2.addTask(new TaskNode(3, "b"), 3);
		p1.addTask(new TaskNode(4, "c"), 2);
		p1.addTask(new TaskNode(5, "d"), 6);

		Schedule correctSolution2 = new Schedule(2, graph);
		List<Processor> processes2 = correctSolution2.getProcessors();

		Processor Twop1 = processes2.get(0);
		Processor Twop2 = processes2.get(1);

		Twop2.addTask(new TaskNode(2, "a"), 0);
		Twop1.addTask(new TaskNode(3, "b"), 3);
		Twop2.addTask(new TaskNode(4, "c"), 2);
		Twop2.addTask(new TaskNode(5, "d"), 6);


		Output.createOutput(processes, graph, parentPath + "optimalSolutionOneCorrectSolution.dot");
		Output.createOutput(processes2, graph, parentPath + "optimalSolutionTwoCorrectSolution.dot");


		boolean same1 = compareTextFiles(parentPath + "optimalSolution.dot", parentPath + "optimalSolutionOneCorrectSolution.dot");
		boolean same2 = compareTextFiles(parentPath + "optimalSolution.dot", parentPath + "optimalSolutionTwoCorrectSolution.dot");

		if (same1) {
			assertTrue(same1);
		}
		else if (same2){
			assertTrue(same2);
		}
		else {
			fail();
		}
	}

	
	@Test
	public void testNode7OptimalTwoProcesses() throws URISyntaxException {
		GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_7_OutTree.dot");

		String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
		File parent = new File(path);
		String parentPath = parent.getParent() + "\\";

		Scheduler schedule = new DFBnBScheduler(graph, 2);
		Schedule solution = schedule.createSchedule();
		
		assertTrue(solution.getBound() == 28);
	}
	
	@Test
	public void testNode8OptimalTwoProcesses() throws URISyntaxException {
		GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_8_Random.dot");

		String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
		File parent = new File(path);
		String parentPath = parent.getParent() + "\\";

		Scheduler schedule = new DFBnBScheduler(graph, 2);
		Schedule solution = schedule.createSchedule();
	
		assertTrue(solution.getBound() == 581);
	}
	
	@Test
	public void testNode9OptimalTwoProcesses() throws URISyntaxException {
		GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_9_SeriesParallel.dot");

		String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
		File parent = new File(path);
		String parentPath = parent.getParent() + "\\";

		Scheduler schedule = new DFBnBScheduler(graph, 2);
		Schedule solution = schedule.createSchedule();
		
		assertTrue(solution.getBound() == 55);
	}
	
	@Test
	public void testNode10OptimalTwoProcesses() throws URISyntaxException {
		GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_10_Random.dot");

		String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
		File parent = new File(path);
		String parentPath = parent.getParent() + "\\";

		Scheduler schedule = new DFBnBScheduler(graph, 2);
		Schedule solution = schedule.createSchedule();
		
		assertTrue(solution.getBound() == 50);
	}
	
	@Test
	public void testNode7Optimal4Processes() throws URISyntaxException {
		GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_7_OutTree.dot");

		String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
		File parent = new File(path);
		String parentPath = parent.getParent() + "\\";

		Scheduler schedule = new DFBnBScheduler(graph, 4);
		Schedule solution = schedule.createSchedule();

		assertTrue(solution.getBound() == 22);
	}
	
	
	@Test
	public void testNode8Optimal4Processes() throws URISyntaxException {
		GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_8_Random.dot");

		String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
		File parent = new File(path);
		String parentPath = parent.getParent() + "\\";

		Scheduler schedule = new DFBnBScheduler(graph, 4);
		Schedule solution = schedule.createSchedule();
		
		assertTrue(solution.getBound() == 581);
	}
	
	@Test
	public void testNode9Optimal4Processes() throws URISyntaxException {
		GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_9_SeriesParallel.dot");

		String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
		File parent = new File(path);
		String parentPath = parent.getParent() + "\\";

		Scheduler schedule = new DFBnBScheduler(graph, 4);
		Schedule solution = schedule.createSchedule();
		
		assertTrue(solution.getBound() == 55);
	}

	@Test
	public void testNode10Optimal4Processes() throws URISyntaxException {
			GraphLoader loader = new GraphLoader();
			TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_10_Random.dot");

			String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
			File parent = new File(path);
			String parentPath = parent.getParent() + "\\";

			Scheduler schedule = new DFBnBScheduler(graph, 4);
			Schedule solution = schedule.createSchedule();

			System.out.println(solution.getBound());
		assertTrue(solution.getBound() == 50);
	}
	
	@Test
	public void testNode11OptimalTwoProcesses() throws URISyntaxException {
		GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_11_OutTree.dot");

		String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
		File parent = new File(path);
		String parentPath = parent.getParent() + "\\";

		Scheduler schedule = new DFBnBScheduler(graph, 2);
		Schedule solution = schedule.createSchedule();
		
		System.out.println(solution.getBound());
		assertTrue(solution.getBound() == 350);
	}
	
	@Test
	public void testNode11OptimalFourProcesses() throws URISyntaxException {
		GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_11_OutTree.dot");

		String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
		File parent = new File(path);
		String parentPath = parent.getParent() + "\\";

		Scheduler schedule = new DFBnBScheduler(graph, 4);
		Schedule solution = schedule.createSchedule();
		
		System.out.println(solution.getBound());
		assertTrue(solution.getBound() == 227);
	}
	
}
