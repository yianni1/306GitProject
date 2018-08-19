package scheduling;

import exceptions.NotDeschedulableException;
import exceptions.NotSchedulableException;
import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;
import java.io.Serializable;
import java.util.*;

import static java.util.Collections.sort;

/**
 * This class shows a partial Schedule to the problem. It will be a node on the Schedule tree
 */
public class Schedule implements Serializable {

    private List<Processor> processors;
    private List<TaskNode> schedulableNodes = new ArrayList<TaskNode>(); // The tasks that can be scheduled.
    private TaskGraph graph;
    private List<TaskNode> scheduleOrder; // The order in which the tasks have been scheduled.
    private Map<String, Integer> map;
    /**
     * Constructor creates a Schedule object, representing a node on the
     * schedule tree
     * @param numberOfProcessors the number of processors
     * @param graph the taskgraph on which the tree is based
     */
    public Schedule(int numberOfProcessors, TaskGraph graph) {

        //Creating the list of processors
        List<Processor> processors = new ArrayList<Processor>();
        for (int i = 0; i < numberOfProcessors; i++) {
            processors.add(new Processor(i));
        }

        //Initialising fields
        this.processors = processors;
        this.graph = graph;
        this.scheduleOrder = new ArrayList<TaskNode>();

        initializeSchedulableNodes(graph);

        map = new HashMap<>();
    }




    public String identify() {
        String str = "";
        for (Processor p : processors) {
            for (TaskNode task : p.getTasks()) {

                map.put(task.getName(), task.getStartTime());
            }
        }
        str = map.keySet().toString() + map.values().toString();
        map.clear();
        return str;
    }

    /**
     * Initialises the 'schedulable nodes' list. (i.e. the entry nodes)
     * In the beginning, the only schedulable nodes will be the entry nodes.
     * Called from the constructor.
     *
     * @return schedulable: the list of schedulable nodes.
     */
    private void initializeSchedulableNodes(TaskGraph tg) {

        //Initialising variables
        List<TaskNode> initialNodes = new ArrayList<TaskNode>();
        HashSet<TaskNode> nodes = tg.getNodes();

        //Looping trough all the nodes, to see if it
        //is an initial node.
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
     * Also sorts the schedulableNodes (the same way each time.
     * @return schedulableNodes
     */
    public List<TaskNode> getSchedulableNodes() {

        this.schedulableNodes = sortSchedulableNodesAlphabetically();
        return this.schedulableNodes;
    }

    /**
     * Sorts all of the schedulable nodes
     * alphabetically, based on their names
     * @return the sorted list of scheduled nodes
     */
    private List<TaskNode> sortSchedulableNodesAlphabetically() {

        // Sort the tasknodes aphabetically.
        List<String> taskNames = new ArrayList<String>();
        for(TaskNode tn: schedulableNodes) {
            taskNames.add(tn.getName());
        }
        sort(taskNames);

        //Duplicating the schedulable nodes
        List<TaskNode> sortedTN = new ArrayList<TaskNode>();
        for(String s: taskNames) {
            for(TaskNode tn: schedulableNodes ) {
                if (s.equals(tn.getName())) {
                    sortedTN.add(tn);
                }
            }
        }
        return sortedTN;
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

        //Looping through its outgoing edges
        //To add the child task if it is schedulable
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

        //Removing the task from its corresponding processor
        for (Processor p : processors) {
            if (p.getTasks().contains(lastScheduledTask)) {
                p.removeTask(lastScheduledTask);
                break;
            }
        }

        // Updating the schedulable nodes.
        // Get the last scheduled node, and add it back. Then remove all it's children from schedulable.
//        TaskNode tn = scheduleOrder.get(scheduleOrder.size()-1);
        schedulableNodes.add(lastScheduledTask);

        //Removing all the schedulable nodes from the outoging edges
        for (TaskEdge e : lastScheduledTask.getOutgoingEdges()) {
            schedulableNodes.remove(e.getEndNode());
        }

        //Removing the task from the list of tasks
        //In the schedule
        scheduleOrder.remove(scheduleOrder.size()-1);
    }

    /**
     * This should only be run if the task is schedulable.
     * It should return the earliest schedulable time.
     *
     * @return An int with the earliest schedulable time for that node on that processor
     */
    public int getEarliestSchedulableTime(TaskNode node, Processor p) {
        int earliestStartTime = -1;

        if (node.isSchedulable()) {
            //Loop through all of its incoming edges to see its end time
            for (TaskEdge e : node.getIncomingEdges()) {
                int endTime = e.getStartNode().getEndTime(); //Getting the end time
                if (endTime > earliestStartTime) {
                    earliestStartTime = endTime;
                }

                //Taking the edge weight into account, if they are scheduled on different processors
                if (!e.getStartNode().getProcessor().equals(p)) {
                    if (earliestStartTime < endTime + e.getWeight()) {
                        earliestStartTime = endTime + e.getWeight();
                    }
                }
            }
            earliestStartTime = Math.max(earliestStartTime, p.getBound());
        }

        return earliestStartTime;
    }

    /**
     * Returns the current bound on the schedule
     * @return the current bound
     */
    public int getBound() {
        int bound = 0;

        //Looping through the processors to get its bound
        for (Processor processor: processors) {
            if (processor.getBound() > bound) {
                bound = processor.getBound();
            }
        }

        return bound;
    }

    /**
     * Retusn the taskgraph
     * @return
     */
    public TaskGraph getGraph() {
        return graph;
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