package testCases;

import exceptions.NotSchedulableException;
import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;
import io.GraphLoader;
import org.junit.Test;
import scheduling.Processor;
import scheduling.Schedule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetEarliestSchedulableTimeTest extends testCases.CompareOutput {

    @Test
    public void testGetEarliestSchedulableTime() throws NotSchedulableException {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/test/java/DotFiles/SuppliedTests/Nodes_7_OutTree.dot");
        Schedule schedule = new Schedule(2, graph);

        TaskNode rootNode = null;

        //test that only root node can be scheduled at time 0 on al processors, otherwise -1 is returned
        for (TaskNode node : graph.getNodes()) {
            for (Processor processor : schedule.getProcessors()) {
                if (node.getName().equals("0")) {
                    if (!node.isScheduled()) {
                        rootNode = node;
                        assertEquals(schedule.getEarliestSchedulableTime(node, processor), 0);
                    }
                } else {
                    assertEquals(schedule.getEarliestSchedulableTime(node, processor), -1);
                }
            }

        }

        schedule.addTask(rootNode, schedule.getProcessors().get(0), 0);

        // test communication cost is added on different processors
        for (TaskNode node : schedule.getSchedulableNodes()) {
            for (Processor processor : schedule.getProcessors()) {
                if (processor.equals(schedule.getProcessors().get(0))) {
                    assertEquals(schedule.getEarliestSchedulableTime(node, processor), 5);
                } else {
                    if (node.getName().equals("1")) {
                        assertEquals(schedule.getEarliestSchedulableTime(node, processor), 20);
                    }
                    if (node.getName().equals("2")) {
                        assertEquals(schedule.getEarliestSchedulableTime(node, processor), 16);
                    }
                    if (node.getName().equals("3")) {
                        assertEquals(schedule.getEarliestSchedulableTime(node, processor), 16);
                    }
                }
            }
        }

        schedule = new Schedule(2, loader.load("src/test/java/DotFiles/SuppliedTests/Nodes_7_OutTree.dot"));

        for (TaskNode node : schedule.getSchedulableNodes()) {
            if (node.getName().equals("g")) {
                assertEquals(schedule.getEarliestSchedulableTime(node, schedule.getProcessors().get(0)), 0);
                schedule.addTask(node, schedule.getProcessors().get(0), 0);

            } else if (node.getName().equals("a")) {
                assertEquals(schedule.getEarliestSchedulableTime(node, schedule.getProcessors().get(0)), 0);
                schedule.addTask(node, schedule.getProcessors().get(1), 0);
            }
        }

        for (TaskNode node : schedule.getSchedulableNodes()) {
            if (node.getName().equals("b")) {
                assertEquals(schedule.getEarliestSchedulableTime(node, schedule.getProcessors().get(0)), 5);
                schedule.addTask(node, schedule.getProcessors().get(0), 5);
            }
        }

        for (TaskNode node : schedule.getSchedulableNodes()) {
            if (node.getName().equals("c")) {
                assertEquals(schedule.getEarliestSchedulableTime(node, schedule.getProcessors().get(0)), 12);
                assertEquals(schedule.getEarliestSchedulableTime(node, schedule.getProcessors().get(1)), 8);
                schedule.addTask(node, schedule.getProcessors().get(0), 12);
            }
        }

        for (TaskNode node : schedule.getSchedulableNodes()) {
            if (node.getName().equals("d")) {
                assertEquals(schedule.getEarliestSchedulableTime(node, schedule.getProcessors().get(0)), 14);
                assertEquals(schedule.getEarliestSchedulableTime(node, schedule.getProcessors().get(1)), 14);

            }
        }

        for (TaskNode node : schedule.getSchedulableNodes()) {
            if (node.getName().equals("f")) {
                assertEquals(schedule.getEarliestSchedulableTime(node, schedule.getProcessors().get(0)), 17);
                assertEquals(schedule.getEarliestSchedulableTime(node, schedule.getProcessors().get(1)), 18);
            }
        }

        for (TaskNode node : schedule.getSchedulableNodes()) {
            if (node.getName().equals("e")) {
                assertEquals(schedule.getEarliestSchedulableTime(node, schedule.getProcessors().get(0)), 25);
                assertEquals(schedule.getEarliestSchedulableTime(node, schedule.getProcessors().get(1)), 27);
            }
        }

    }

    @Test
    public void testCustomTest() {
        TaskNode nodeA = new TaskNode(6, "a");
        TaskNode nodeB = new TaskNode(7, "b");
        TaskNode nodeC = new TaskNode(2, "c");
        TaskNode nodeD = new TaskNode(3, "d");
        TaskNode nodeF = new TaskNode(8, "f");
        TaskNode nodeG = new TaskNode(5, "g");

        TaskEdge edge1 = new TaskEdge(nodeA, nodeC, 4);
        TaskEdge edge2 = new TaskEdge(nodeG, nodeC, 3);
        TaskEdge edge3 = new TaskEdge(nodeB, nodeC, 6);
        TaskEdge edge4 = new TaskEdge(nodeB, nodeD, 2);
        TaskEdge edge5 = new TaskEdge(nodeD, nodeF, 1);



        nodeC.addIncomingEdge(edge1);
        nodeC.addIncomingEdge(edge2);
        nodeC.addIncomingEdge(edge3);
        nodeD.addIncomingEdge(edge4);
        nodeF.addIncomingEdge(edge5);

        nodeA.addOutgoingEdge(edge1);
        nodeB.addOutgoingEdge(edge2);
        nodeG.addOutgoingEdge(edge3);
        nodeB.addOutgoingEdge(edge4);
        nodeD.addOutgoingEdge(edge5);

        TaskGraph graph = new TaskGraph("graph");
        graph.addEdge(edge1);
        graph.addEdge(edge2);
        graph.addEdge(edge3);
        graph.addEdge(edge4);
        graph.addEdge(edge5);

        graph.addNode(nodeA);
        graph.addNode(nodeB);
        graph.addNode(nodeC);
        graph.addNode(nodeD);
        graph.addNode(nodeF);
        graph.addNode(nodeG);

        //Creates schedule with 2 processors
        Schedule schedule = new Schedule(3, graph);

        schedule.addTask(nodeA, schedule.getProcessors().get(0), 0);
        schedule.addTask(nodeB, schedule.getProcessors().get(1), 0);
        schedule.addTask(nodeG, schedule.getProcessors().get(2), 0);
        schedule.addTask(nodeD, schedule.getProcessors().get(1), 7);
        schedule.addTask(nodeF, schedule.getProcessors().get(0), 11);

        for (int i=0; i<20; i++) {
            int cool = schedule.getEarliestSchedulableTime(nodeC, schedule.getProcessors().get(1));
            System.out.print(cool + " ");
        }
        System.out.println();


        //Tests bound of max processor in schedule when A is added on p1
       // assertTrue(schedule.getBound() == 5);

      //  schedule.addTask(nodeB, schedule.getProcessors().get(1), 6);
    }

}
