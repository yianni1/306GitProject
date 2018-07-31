package scheduling;

import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by olive on 29/07/2018.
 */
public class SolutionTree {

	private List<Processor> processorList;
	private TaskGraph graph;

	private HashSet<TaskNode> initialNodes;

	/**
	 * Constructor builds a solution tree, with all the solutions
	 * @param startingNodes
	 * @param processorNumber
	 */
	public SolutionTree(TaskGraph graph, int processors) {

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
		initialNodes = schedule.getSchedulableNodes();

		int minimialInitialNodeweight = 0;

		TaskNode initialNode = null;

		//Adds initial nodes with no incoming edges to processor
		for (TaskNode taskNode : initialNodes) {

			// Gets node with minimal weight and keeps it, to be scheduled on processor
			int weight = taskNode.getWeight();
			if (minimialInitialNodeweight < weight) { 
				minimialInitialNodeweight = weight;
				initialNode = taskNode;
			}
		}

		//Schedules minimal node with minimal weight first
		List<Processor> processorList = schedule.getProcessors();
		processorList.get(0).addTask(initialNode, initialNode.getWeight());

		initialNodes.remove(initialNode);

		
		List<TaskNode> scheduableChildren = new ArrayList<TaskNode>();

		// Checks child nodes of initial node
		for (TaskEdge edge : initialNode.getOutgoingEdges()) {

			TaskNode currentchildNode = edge.getEndNode();

			//Checks if node had scheduled parents, and if so, set this node to be scheduled
			if (!currentchildNode.isSchedulable()) {
				scheduableChildren.add(currentchildNode);

			}
			else {
				newInitialNode(currentchildNode);
			}
		}


		//Loop to determine which processor to schedule nodes
		for (TaskNode node : scheduableChildren) {

			
			for (Processor processor : processorList) {

				boolean scheduled = false;
				
				//If schedule is not empty
				if (processor.getTasks().size() != 0) {
					
					//If Schedule does not already contain node
					if (!processor.getTasks().contains(node)) {
						
						//Get latestTask from processor
						TaskNode latestTask = processor.getTasks().get(processor.getTasks().size() - 1);

						//If latest Task == current node parent, schedule this node on this processor for 
						//No switching processor edge cost
						for (TaskEdge edgeB : node.getIncomingEdges()) {
							if (latestTask.equals(edgeB.getStartNode())) {
								processor.addTask(node, node.getWeight());
								scheduled = true;
								break;
							}
						}

						//If current schedule does is not parent node
						for (TaskEdge edgeA : latestTask.getIncomingEdges()) {
							for (TaskEdge edgeB : node.getIncomingEdges()) {
								
								//Ensures parent nodes same for latest task node and current toSchedule node
								if (edgeB.getStartNode().equals(edgeA.getStartNode())) {

									//Determines the processing time for staying on current processor 
									//And for scheduling on other processor
									int differentProcessorTime = initialNode.getWeight() + edgeB.getWeight();
									int sameProcessorTime = processor.getCost();

									//Checks which processor to schedule on
									if (differentProcessorTime < sameProcessorTime) {
										
										//Schedules node on other processor 
										for (Processor otherProcessor : processorList) {
											if (!otherProcessor.equals(processor)) {
												otherProcessor.addTask(node, differentProcessorTime + node.getWeight());
												scheduled = true;
												break;
											}
										}

									}
									//Schedules node on same processor
									else {
										processor.addTask(node, node.getWeight());
										scheduled = true;
										break;
									}

								}
							}
						}
					}
				}
				else {
					//INCORRECT WHAT TO DO WHEN CURRENT PROCESSOR HAS NO CURRENT SCHEDULE?????
					if (node.isScheduled()) {
						for (TaskEdge edge : node.getIncomingEdges()) {
							if (edge.getStartNode().equals(initialNode)) {
								int differentProcessorTime = edge.getStartNode().getWeight() + initialNode.getWeight();
								int sameProcessorTime = initialNode.getWeight();

								if (differentProcessorTime > sameProcessorTime) {
									processor.addTask(node, differentProcessorTime);
								}
							}

						}
					}
				}
				//Break if current node is scheduled
				if (scheduled) {
					break;
				}
			}

		}


		System.out.println(processorList);


	}

	/**
	 * Schedules another initial node
	 */
	private void newInitialNode(TaskNode smallestTaskNode) {
		HashSet<TaskEdge> edges = smallestTaskNode.getIncomingEdges();

		boolean hasAllParentsScheduled = true;
		for (TaskEdge edge : edges) {
			TaskNode startNode = edge.getStartNode();

			if (!startNode.isScheduled()) {
				hasAllParentsScheduled = false;
				break;
			}
		}

	}


}
