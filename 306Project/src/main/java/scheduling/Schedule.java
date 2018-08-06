package scheduling;

import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;

import javax.xml.soap.Node;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This class shows a partial Schedule to the problem. It will be a node on the Schedule tree
 */
public class Schedule {

    private List<Processor> processors = new ArrayList<Processor>();
    private List<TaskNode> schedulableNodes = new ArrayList<TaskNode>(); // The tasks that can be scheduled.
    private TaskNode lastScheduledTask;
    private TaskGraph graph;

    public Schedule(int numberOfProcessors, TaskGraph graph) {
        List<Processor> processors = new ArrayList<Processor>();
        for (int i = 0; i < numberOfProcessors; i++) {
            processors.add(new Processor(i));
        }

        this.processors = processors;
        this.lastScheduledTask = null;
        this.graph = graph;

        initializeSchedulableNodes(graph);

    }

    /**
     * Initialises the 'schedulable nodes' list. (i.e. the entry nodes)
     * In the beginning, the only schedulable nodes will be the entry nodes.
     * Called from the constructor.
     *
     * @return schedulable: the list of schedulable nodes.
     */
    private void initializeSchedulableNodes(TaskGraph tg) {
        List<TaskNode> initialNodes = new ArrayList<TaskNode>();
        HashSet<TaskNode> nodes = tg.getNodes();

        for (TaskNode n : nodes) {
            if (n.getIncomingEdges().size() == 0) {
            	initialNodes.add(n);
            }
        }
        this.schedulableNodes = initialNodes;
    }

    /**
     * Returns the processors that this schedule contains.
     *
     * @return processors: the list of processors.
     */
    public List<Processor> getProcessors() {	
        return processors;
    }
    
    
    /**
     * Updates the schedulable nodes (for after a node get scheduled).
     *
     * @param tn is the node that has just been scheduled.
     */
    public void updateSchedulableNodes(TaskNode tn) {
        schedulableNodes.remove(tn);

        for (TaskEdge e : tn.getOutgoingEdges()) {
            if (e.getEndNode().isSchedulable()) {
                schedulableNodes.add(e.getEndNode());
            }
        }
    }

    /**
     * Gets all the nodes that have been scheduled on this schedule.
     *
     * @return scheduledNodes: all the nodes that have been scheduled.
     */
    public List<TaskNode> getScheduledNodes() {
        List<TaskNode> scheduledNodes = new ArrayList<TaskNode>();
        for (Processor processor : processors) {
            scheduledNodes.addAll(processor.getTasks());
        }

        return scheduledNodes;
    }

    /**
     * Gets all nodes that are able to be scheduled.
     *
     * @return schedulableNodes
     */
    public List<TaskNode> getSchedulableNodes() {
        return this.schedulableNodes;
    }

    /**
     * Adds a task to the current schedule.
     *
     * @param node the node to be added
     * @param processor the processor to add it to
     */
    public void addTask(TaskNode node, Processor processor, int time) {
        processor.addTask(node, time);
        lastScheduledTask = node;
    }

    /**
     * Removes the last scheduled task.
     */
    public void removeLastScheduledTask() {
        for (Processor processor : processors) {
            if (processor.getTasks().contains(lastScheduledTask)) {
                processor.removeTask(lastScheduledTask);
            }
        }
    }

    /**
     * This should only be run if the task is schedulable.
     * It should return the earliest schedulable time.
     *
     * @return
     */
    public int getEarliestSchedulableTime(TaskNode node, Processor p) {
        int earliestStartTime = -1;

        if (node.isSchedulable()) {
            for (TaskEdge e : node.getIncomingEdges()) {
                int endTime = e.getStartNode().getEndTime();             
                if (endTime > earliestStartTime) {
                    earliestStartTime = endTime;
                }
                if (!e.getStartNode().getProcessor().equals(p)) {
                    if (earliestStartTime < e.getStartNode().getEndTime() + e.getWeight()) {
                        earliestStartTime = earliestStartTime + e.getWeight();
                    }
                }
            }
        }

        return Math.max(earliestStartTime, p.getBound());
    }


}
