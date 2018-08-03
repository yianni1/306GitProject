//package scheduling;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//
//import graph.TaskEdge;
//import graph.TaskGraph;
//import graph.TaskNode;
//import io.Output;
//
//public class GreedySchedule {
//
//	private List<Processor> processorList;
//	private TaskGraph graph;
//
//	/**
//	 * Constructor builds a solution tree, with all the solutions
//	 * @param startingNodes
//	 * @param processorNumber
//	 */
//	public GreedySchedule(TaskGraph graph, int processors) {
//
//		processorList = new ArrayList<Processor>();
//
//		for (int i = 0; i < processors; i++) {
//			processorList.add(new Processor(i));
//		}
//		this.graph = graph;
//
//	}
//
//
//	/**
//	 * Implementation of the graph scheduling
//	 */
//	public Schedule doSchedule() {
//
//		Schedule schedule = new Schedule(processorList.size(), graph);
//
//		while (schedule.getSchedulableNodes().size() > 0) {
//
//			@SuppressWarnings("unchecked")
//			ArrayList<TaskNode> avalibleNodes = (ArrayList<TaskNode>) schedule.getSchedulableNodes().clone();
//			List<Processor> processors = schedule.getProcessors();
//
//			//Loop through Available nodes
//			for (TaskNode node : avalibleNodes) {
//				boolean scheduled = false;
//				//Loop through each processor
//				for (Processor processor : processors) {
//
//					//If node is an initial node, simply schedule it on first processor
//					if (node.getIncomingEdges().size() == 0) {
//						processor.addTask(node, node.getWeight());
//						schedule.updateSchedulableNodes(node);
//						break;
//					}
//					else {
//
//						List<TaskNode> parents = new ArrayList<TaskNode>();
//						List<TaskNode> elementsAfterParent = new ArrayList<TaskNode>();
//
//						//Get the parent nodes for the current to be scheduled node
//						for (TaskEdge edgeParents : node.getIncomingEdges()) {
//							parents.add(edgeParents.getStartNode());
//						}
//
//						int parentWeight = 0;
//						//Get the min weight from its parents indicating the min start time of the node
//						for (TaskNode parent : parents) {
//							if (parent.getEndTime() > parentWeight) {
//								parentWeight = parent.getEndTime();
//							}
//						}
//
//						// Determine which is greater, the parent weight cost, or the current processor total cost
//						int sameProcessorCost = 0;
//						//if (parentWeight >= processor.getCost()) {
//						///	sameProcessorCost = parentWeight;
//						//}
//						//else {
//							sameProcessorCost = processor.getBound();
//						//}
//
//						int bestOtherProcessorCost = Integer.MAX_VALUE;
//						Processor bestP = processor;
//						//Loop through other processors to see if there is a better schedule
//						for (Processor otherProcessor : processors) {
//							if(!otherProcessor.equals(processor)) {
//
//								TaskEdge edge = null;
//								for (TaskEdge e : node.getIncomingEdges()) {
//									if (edge == null) {
//										edge = e;
//									}
//									if (edge.getWeight() > e.getWeight()) {
//										edge = e;
//									}
//								}
//
//								int differentProcessorCost =  parentWeight + edge.getWeight() + otherProcessor.getBound();
//								if (bestOtherProcessorCost > differentProcessorCost) {
//									bestOtherProcessorCost = differentProcessorCost;
//									bestP = otherProcessor;
//								}
//							}
//						}
//
//						if (sameProcessorCost < bestOtherProcessorCost) {
//							if (!processor.getTasks().contains(node)) {
//								processor.addTask(node, sameProcessorCost + node.getWeight());
//								schedule.updateSchedulableNodes(node);
//								scheduled = true;
//							}
//						}
//						else {
//
//							bestP.addTask(node, bestOtherProcessorCost + node.getWeight());
//							schedule.updateSchedulableNodes(node);
//							scheduled = true;
//						}
//						if (scheduled) {
//							break;
//						}
//					}
//					if (scheduled) {
//						break;
//					}
//				}
//
//				if (scheduled) {
//					break;
//				}
//
//
//
//			}
//		}
//
//		return schedule;
//
//
//	}
//
//}
