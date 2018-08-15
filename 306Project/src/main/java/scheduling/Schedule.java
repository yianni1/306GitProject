package scheduling;

import exceptions.NotDeschedulableException;
import exceptions.NotSchedulableException;
import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;
import javafx.concurrent.Task;

import javax.xml.soap.Node;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.util.Collections.sort;

/**
 * This class shows a partial Schedule to the problem. It will be a node on the Schedule tree
 */
public class Schedule implements Serializable {

    private List<Processor> processors = new ArrayList<Processor>();
    private List<TaskNode> schedulableNodes = new ArrayList<TaskNode>(); // The tasks that can be scheduled.
    private TaskGraph graph;
    private List<TaskNode> scheduleOrder; // The order in which the tasks have been scheduled.

    public Schedule(int numberOfProcessors, TaskGraph graph) {
        List<Processor> processors = new ArrayList<Processor>();
        for (int i = 0; i < numberOfProcessors; i++) {
            processors.add(new Processor(i));
        }

        this.processors = processors;
        this.graph = graph;
        this.scheduleOrder = new ArrayList<TaskNode>();

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
     * Gets all the nodes that have been scheduled on this schedule.
     *
     * @return scheduledNodes: all the nodes that have been scheduled.
     */
    public List<TaskNode> getScheduledNodes() {
        return scheduleOrder;
    }

    /**
     * Gets all nodes that are able to be scheduled.
     *
     * @return schedulableNodes
     */
    public List<TaskNode> getSchedulableNodes() {

        // Sort the tasknodes aphabetically.
        List<String> taskNames = new ArrayList<String>();
        for(TaskNode tn: schedulableNodes) {
           taskNames.add(tn.getName());
        }
        sort(taskNames);

        List<TaskNode> sortedTN = new ArrayList<TaskNode>();
        for(String s: taskNames) {
            for(TaskNode tn: schedulableNodes ) {
                if (s.equals(tn.getName())) {
                    sortedTN.add(tn);
                }
            }
        }

        this.schedulableNodes = sortedTN;

//        System.out.print("Schedulable nodes are: ");
//        for(TaskNode tn: schedulableNodes) {
//            System.out.print(tn.getName() + ", ");
//        }
        return this.schedulableNodes;
    }

    /**
     * Adds a task to the current schedule.
     *
     * @param node the node to be added
     * @param processor the processor to add it to
     */
    public void addTask(TaskNode node, Processor processor, int time) throws NotSchedulableException {
        processor.addTask(node, time);
        scheduleOrder.add(node);

        // Updates the schedulable nodes.
        schedulableNodes.remove(node);

        for (TaskEdge e : node.getOutgoingEdges()) {
            if (e.getEndNode().isSchedulable()) {
                schedulableNodes.add(e.getEndNode());
            }
        }
    }

    /**
     * Removes the last scheduled task.
     */
    public void removeLastScheduledTask() throws NotDeschedulableException {
        TaskNode lastScheduledTask = scheduleOrder.get(scheduleOrder.size() - 1);

        for (Processor p : processors) {
            if (p.getTasks().contains(lastScheduledTask)) {
                p.removeTask(lastScheduledTask);
            }
        }

        // Updating the schedulable nodes.
        // Get the last scheduled node, and add it back. Then remove all it's children from schedulable.
//        TaskNode tn = scheduleOrder.get(scheduleOrder.size()-1);
        schedulableNodes.add(lastScheduledTask);

        for (TaskEdge e : lastScheduledTask.getOutgoingEdges()) {
//            if (e.getEndNode().isSchedulable()) {
                schedulableNodes.remove(e.getEndNode());
//            }
        }

        scheduleOrder.remove(scheduleOrder.size()-1);
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
                        earliestStartTime = endTime + e.getWeight();
                    }
                }
            }
            earliestStartTime = Math.max(earliestStartTime, p.getBound());
        }

        if (node.getName().equals("c") && p.getID()== 1 && ( earliestStartTime==11 || earliestStartTime==10)) {
            //System.out.println("Task C on p1: EST is " + earliestStartTime);
            earliestStartTime = -1;
            if (node.isSchedulable()) {
                for (TaskEdge e : node.getIncomingEdges()) {
                    int endTime = e.getStartNode().getEndTime();
                    System.out.print(e.getStartNode().getName() + " ");
                    System.out.print("End time: " + endTime + ", EST: " + earliestStartTime + ". ");

                    if (endTime > earliestStartTime) {
                        earliestStartTime = endTime;
                    }
                    if (!e.getStartNode().getProcessor().equals(p)) {
                        if (earliestStartTime < endTime + e.getWeight()) {
                            earliestStartTime = endTime + e.getWeight();
                        }
                    }
                    System.out.print("EST2: " + earliestStartTime + ". \t");
                }
                earliestStartTime = Math.max(earliestStartTime, p.getBound());
                System.out.println("EST2: " + earliestStartTime + ". ");
            }
        }

        return earliestStartTime;
    }

    public int getBound() {
        int bound = 0;
        for (Processor processor: processors) {
            if (processor.getBound() > bound) {
                bound = processor.getBound();
            }
        }

        return bound;
    }

    public void printSchedule(){
        for(Processor p: processors){
            System.out.println("Processor " + p.getID() + ":");
            for (TaskNode tn: p.getTasks()) {
                System.out.println(tn.getName() + ", start time " + tn.getStartTime());
            }
        }

        System.out.println("In the order: ");
        for(TaskNode tn: scheduleOrder) {
            System.out.print( tn.getName() + " ");
        }
        System.out.println();
    }


}
