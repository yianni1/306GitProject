package scheduling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import graph.TaskGraph;
import graph.TaskNode;
import io.Output;

public class SimpleScheduler implements Scheduler{


	public SimpleScheduler(TaskGraph graph, int processors) {
		
	}
	
	
	/**
	 * Implementation of the graph scheduling
	 */
	public Schedule createSchedule() {

//		Schedule schedule = new Schedule(numProcessors, graph);
//		Processor processor = schedule.getProcessors().get(0);
		
//		while (schedule.getSchedulableNodes().size() > 0) {
//
//			@SuppressWarnings("unchecked")
//			HashSet<TaskNode> avalibleNodes = (HashSet<TaskNode>) schedule.getSchedulableNodes().clone();
//			for (TaskNode node : avalibleNodes) {
//				processor.addTask(node, node.getWeight());
//				schedule.updateSchedulableNodes(node);
//			}
//		}
//
//
//		Output.setOutputFileName("output");
//		try {
//			Output.createOutput(processor, graph);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
		return null;
		
		
		
		
	}
	
	
	
}
