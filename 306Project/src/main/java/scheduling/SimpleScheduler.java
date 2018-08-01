package scheduling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import graph.TaskGraph;
import graph.TaskNode;
import io.Output;

public class SimpleScheduler {


	private List<Processor> processorList;
	private TaskGraph graph;
	
	/**
	 * Constructor builds a solution tree, with all the solutions
	 * @param startingNodes
	 * @param processorNumber
	 */
	public SimpleScheduler(TaskGraph graph, int processors) {

		processorList = new ArrayList<Processor>();

		for (int i = 0; i < processors; i++) {
			processorList.add(new Processor(i));
		}
		this.graph = graph;

	}
	
	
	/**
	 * Implementation of the graph scheduling
	 */
	public void doSchedule() {

		Schedule schedule = new Schedule(processorList);
		schedule.initializeSchedulableNodes(graph);
		Processor processor = processorList.get(0);
		
		while (schedule.getSchedulableNodes().size() > 0) {
			
			@SuppressWarnings("unchecked")
			HashSet<TaskNode> avalibleNodes = (HashSet<TaskNode>) schedule.getSchedulableNodes().clone();
			for (TaskNode node : avalibleNodes) {
				processor.addTask(node, node.getWeight());
				schedule.updateSchedulableNodes(node);
			}
		}


		Output.setOutputFileName("output");
		try {
			Output.createOutput(processor, graph);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		
		
		
		
	}
	
	
	
}
