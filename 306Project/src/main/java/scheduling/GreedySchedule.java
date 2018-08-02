package scheduling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;
import io.Output;

public class GreedySchedule {

	private List<Processor> processorList;
	private TaskGraph graph;

	/**
	 * Constructor builds a solution tree, with all the solutions
	 * @param startingNodes
	 * @param processorNumber
	 */
	public GreedySchedule(TaskGraph graph, int processors) {

		processorList = new ArrayList<Processor>();

		for (int i = 0; i < processors; i++) {
			processorList.add(new Processor(i));
		}
		this.graph = graph;

	}


	/**
	 * Implementation of the graph scheduling
	 */
	public Schedule doSchedule() {

		Schedule schedule = new Schedule(processorList);
		schedule.initializeSchedulableNodes(graph);

		while (schedule.getSchedulableNodes().size() > 0) {

			@SuppressWarnings("unchecked")
			HashSet<TaskNode> avalibleNodes = (HashSet<TaskNode>) schedule.getSchedulableNodes().clone();
			List<Processor> processors = schedule.getProcessors();
			for (TaskNode node : avalibleNodes) {
				boolean scheduled = false;
				for (Processor processor : processors) {

					if (node.getIncomingEdges().size() == 0) {
						processor.addTask(node, node.getWeight());
						schedule.updateSchedulableNodes(node);
						break;
					}
					else {

						List<TaskNode> parents = new ArrayList<TaskNode>();
						List<TaskNode> elementsAfterParent = new ArrayList<TaskNode>();

						for (TaskEdge edgeParents : node.getIncomingEdges()) {
							parents.add(edgeParents.getStartNode());
						}

						int parentWeight = 0;
						for (TaskNode parent : parents) {

							parentWeight = parentWeight + parent.getWeight() + parent.getStartTime();
						}

						int sameProcessorCost = 0;
						if (parentWeight >= processor.getCost()) {
							sameProcessorCost = parentWeight;
						}
						else {
							sameProcessorCost = processor.getCost();
						}
						

						for (Processor otherProcessor : processors) {
							if(!otherProcessor.equals(processor)) {
								
								TaskEdge edge = null;
								for (TaskEdge e : node.getIncomingEdges()) {
									if (edge == null) {
										edge = e;
									}
									if (edge.getWeight() > e.getWeight()) {
										edge = e;
									}
								}
								int differentProcessorCost =  parentWeight + edge.getWeight() + otherProcessor.getCost();

								if (sameProcessorCost < differentProcessorCost) {
									if (!processor.getTasks().contains(node)) {
										processor.addTask(node, sameProcessorCost + node.getWeight());
										schedule.updateSchedulableNodes(node);
										scheduled = true;
									}
								}
								else {
									otherProcessor.addTask(node, differentProcessorCost + node.getWeight());
									schedule.updateSchedulableNodes(node);
									scheduled = true;
								}
								if (scheduled) {
									break;
								}
							}
						}
					}
					if (scheduled) {
						break;
					}
				}

				if (scheduled) {
					break;
				}



			}
		}

		return schedule;


	}

}
