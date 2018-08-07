package testCases;

import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;
import org.junit.Test;
import static org.junit.Assert.*;
import scheduling.Processor;
import scheduling.Schedule;

import java.util.List;

public class ScheduleClassTests {

    /**
     * Tests the addTask method in schedule
     * Should add the input node to the input processor at a certain time
     */
    @Test
    public void testScheduleAddTask() {

        //Sets up a simple TaskGraph
        TaskNode nodeA = new TaskNode(5, "a");
        TaskNode nodeB = new TaskNode(8, "b");
        TaskEdge edge = new TaskEdge(nodeA, nodeB, 1);
        nodeB.addIncomingEdge(edge);
        nodeA.addOutgoingEdge(edge);
        TaskGraph graph = new TaskGraph("graph");
        graph.addEdge(edge);
        graph.addNode(nodeA);
        graph.addNode(nodeB);

        //Creates schedule with 1 processors
        Schedule schedule = new Schedule(1, graph);

        //Checks that only a is initially schedulable nodes are schedualable
        assertTrue(schedule.getSchedulableNodes().size() == 1);
        assertTrue(schedule.getSchedulableNodes().get(0).getName().equals(nodeA.getName()));

        List<Processor> processor = schedule.getProcessors();
        //add task method to test
        schedule.addTask(nodeA, processor.get(0), 0);

        List<TaskNode> schedulableNodes = schedule.getSchedulableNodes();

        //Tests that only other node schedulable is B
        assertTrue(schedulableNodes.size() == 1);
        assertTrue(schedulableNodes.get(0).getName().equals(nodeB.getName()));
    }


    /**
     * Tests the getScheduledNodes method in Schedule
     * Should return the current scheduled nodes of the graph
     * i.e. nodes scheduled on any processor
     */
    @Test
    public void testScheduleGetScheduledNodes() {

        //Sets up a simple TaskGraph
        TaskNode nodeA = new TaskNode(5, "a");
        TaskNode nodeB = new TaskNode(8, "b");
        TaskNode nodeC = new TaskNode(3, "c");
        TaskEdge edgeAB = new TaskEdge(nodeA, nodeB, 1);
        TaskEdge edgeCD = new TaskEdge(nodeC, nodeB, 2);
        nodeB.addIncomingEdge(edgeAB);
        nodeB.addIncomingEdge(edgeCD);

        nodeA.addOutgoingEdge(edgeAB);
        nodeC.addOutgoingEdge(edgeCD);

        TaskGraph graph = new TaskGraph("graph");
        graph.addEdge(edgeAB);
        graph.addEdge(edgeCD);
        graph.addNode(nodeC);
        graph.addNode(nodeA);
        graph.addNode(nodeB);

        //Creates schedule with 1 processors
        Schedule schedule = new Schedule(1, graph);

        List<TaskNode> scheduledNodes = schedule.getScheduledNodes();

        //Test no nodes scheduled initially
        assertTrue(scheduledNodes.size() == 0);

        schedule.addTask(nodeC, schedule.getProcessors().get(0), 0);

        scheduledNodes = schedule.getScheduledNodes();

        assertTrue(scheduledNodes.size() == 1);
        assertTrue(scheduledNodes.get(0).getName().equals(nodeC.getName()));
    }

    /**
     * Tests the initializeSchedulableNodes method in Schedule
     * Should initialise the nodes in the graph with no incoming edges (entry nodes)
     */
    @Test
    public void testScheduleInitializeSchedulableNodes() {
        //Sets up a simple TaskGraph
        TaskNode nodeA = new TaskNode(5, "a");
        TaskNode nodeB = new TaskNode(8, "b");
        TaskNode nodeC = new TaskNode(3, "c");
        TaskEdge edgeAB = new TaskEdge(nodeA, nodeB, 1);
        TaskEdge edgeCD = new TaskEdge(nodeC, nodeB, 2);
        nodeB.addIncomingEdge(edgeAB);
        nodeB.addIncomingEdge(edgeCD);

        nodeA.addOutgoingEdge(edgeAB);
        nodeC.addOutgoingEdge(edgeCD);

        TaskGraph graph = new TaskGraph("graph");
        graph.addEdge(edgeAB);
        graph.addEdge(edgeCD);
        graph.addNode(nodeC);
        graph.addNode(nodeA);
        graph.addNode(nodeB);

        //Creates schedule with 1 processors
        Schedule schedule = new Schedule(1, graph);

        assertTrue(schedule.getSchedulableNodes().size() == 2);

        List<TaskNode> schedulableNode = schedule.getSchedulableNodes();

        //Tests that nodes c and a have been initialized as schedulable
        if (schedulableNode.get(0).getName().equals(nodeA.getName())) {
            assertTrue(schedulableNode.get(1).getName().equals(nodeC.getName()));
        }
        else if (schedulableNode.get(1).getName().equals(nodeA.getName())){
            assertTrue(schedulableNode.get(0).getName().equals(nodeC.getName()));
        }
        else {
            fail();
        }

    }

    /**
     * Tests the schedule.getBound() method in Schedule
     * Should return the bound of the maximal processor in the schedule
     */
    @Test
    public void testScheduleGetBound() {
        //Sets up a simple TaskGraph
        TaskNode nodeA = new TaskNode(5, "a");
        TaskNode nodeB = new TaskNode(8, "b");
        TaskEdge edge = new TaskEdge(nodeA, nodeB, 1);
        nodeB.addIncomingEdge(edge);
        nodeA.addOutgoingEdge(edge);
        TaskGraph graph = new TaskGraph("graph");
        graph.addEdge(edge);
        graph.addNode(nodeA);
        graph.addNode(nodeB);

        //Creates schedule with 2 processors
        Schedule schedule = new Schedule(2, graph);

        schedule.addTask(nodeA, schedule.getProcessors().get(0), 0);

        //Tests bound of max processor in schedule when A is added on p1
        assertTrue(schedule.getBound() == 5);

        schedule.addTask(nodeB, schedule.getProcessors().get(1), 6);

        //Tests bound of max processor in schedule when B is added on p2
        assertTrue(schedule.getBound() == 14);
    }


}

