package scheduling;

import exceptions.NotSchedulableException;
import exceptions.NotDeschedulableException;
import graph.TaskGraph;
import graph.TaskNode;
import pt.runtime.TaskID;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ray on 28/07/2018.
 * Written by Kevin & Ray.
 */
public class DFBnBParrallel {

	private TaskGraph graph;
	private int upperBound;
	private int depth;
	private Processor currentProcessor;

	// Index of the children of the schedule.
	private List<Integer> nodeIndices;
	private List<Integer> processorIndices;

	private Schedule optimalSchedule;
	private Schedule schedule;
	private List<TaskNode> schedulableNodes;

	public DFBnBParrallel(TaskGraph graph, int processors) {
		this.graph = graph;

		nodeIndices = new ArrayList<Integer>();
		processorIndices = new ArrayList<Integer>();
		schedule = new Schedule(processors, graph);
		schedulableNodes = schedule.getSchedulableNodes();

		//initialize depth, upperBound, and current time of the schedule
		depth = 0;

		Schedule greedySchedule = new GreedyScheduler(graph, processors).createSchedule();

		upperBound = greedySchedule.getBound();

		while (greedySchedule.getScheduledNodes().size() > 0) {
			greedySchedule.removeLastScheduledTask();
		}

	}


	public Schedule createSchedule(int maxDepth, ArrayList<Integer> nodIn) throws NotDeschedulableException, NotSchedulableException {
		//initialize upperBound

		TaskNode nextTask = null;
		Processor nextProcessor = null;
		int nodeIndex = 0;
		int processorIndex = 0;

		List<Integer> combinations = new ArrayList<Integer>();
		int combination = 0;
		
		combination = schedule.getProcessors().size() * schedulableNodes.size();
		combinations.add(combination);
		HashMap<TaskNode, Integer> map = new HashMap<TaskNode, Integer>();
		
		for (TaskNode node : schedulableNodes) {
			TaskNode newNode = null;
			try {
				//newNode = node.clone();
			}
			catch (Exception e) {
				
			}		
			map.put(newNode, new Integer(1));
		}
		
		//while there are branches to explore from depth 0, keep looping through all branches
		while (schedulableNodes.size() > 0) { //while there are still nodes to schedule
			
		}
		
		return optimalSchedule;
	}
}

	
