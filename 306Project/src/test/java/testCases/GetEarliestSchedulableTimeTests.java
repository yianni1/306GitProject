package testCases;

import graph.TaskGraph;
import graph.TaskNode;
import io.GraphLoader;
import org.junit.Test;
import scheduling.Processor;
import scheduling.Schedule;

import static org.junit.Assert.assertEquals;

public class GetEarliestSchedulableTimeTests extends testCases.CompareOutput {

    @Test
    public void testGetEarliestSchedulableTime() {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_7_OutTree.dot");
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

        schedule = new Schedule(2, loader.load("src/main/resources/DotFiles/Nodes_7_OutTree.dot"));

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

}}
