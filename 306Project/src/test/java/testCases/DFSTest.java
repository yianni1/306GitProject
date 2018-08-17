package testCases;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import graph.TaskGraph;
import graph.TaskNode;
import io.GraphLoader;
import scheduling.DFBnBScheduler;
import scheduling.Schedule;
import scheduling.Scheduler;

public class DFSTest {

	/**
	 * Tests to ensure bottom level path method is correct
	 * According to graph on taskScheduling slides provided by oliver
	 */
	@Test
	public void test() {

		GraphLoader loader = new GraphLoader();
		TaskGraph graph = loader.load("src/main/resources/DotFiles/DFSTest.dot");
		
		
		DFBnBScheduler schedule = new DFBnBScheduler(graph, 2);
		Map<String, Integer> weights = new HashMap<String, Integer>();
		for (TaskNode n : graph.getNodes()) {

			int maxWeight = schedule.criticalPath(n);
			weights.put(n.getName(), maxWeight);
			
		}
		
		Map<String, Integer> correctWeights = new HashMap<String, Integer>();
		
		correctWeights.put("a", 14);
		correctWeights.put("b", 12);
		correctWeights.put("c", 8);
		correctWeights.put("d", 12);
		correctWeights.put("e", 11);
		correctWeights.put("f", 8);
		correctWeights.put("g", 4);
		correctWeights.put("h", 7);
		correctWeights.put("i", 4);
		correctWeights.put("j", 5);
		correctWeights.put("k", 2);
		
		assertEquals(correctWeights, weights);
		
		
		
	}

}
