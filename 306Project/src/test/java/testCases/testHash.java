package testCases;

import graph.TaskGraph;
import graph.TaskNode;
import io.GraphLoader;
import main.App;
import org.junit.Test;
import scheduling.DFBnBScheduler;
import scheduling.Processor;
import scheduling.Schedule;
import scheduling.Scheduler;

import java.io.File;

import static junit.framework.TestCase.assertTrue;
import static scheduling.Utilities.deepClone;

public class testHash {

    @Test
    public void test() {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/main/resources/DotFiles/CustomTest.dot");

        TaskNode a1 = null;
        TaskNode a2 = null;
        for (TaskNode node : graph.getNodes()) {
            if (node.getName().equals("a")) {
                a1 = (TaskNode) deepClone(node);
                a2 = (TaskNode) deepClone(node);
            }
            if (a1 != null) {
                break;
            }
        }


        Schedule correctSchedule1 = new Schedule(2, graph);
        Processor p1 = correctSchedule1.getProcessors().get(0);
        correctSchedule1.addTask(a1, p1, correctSchedule1.getEarliestSchedulableTime(a1, p1));

        Schedule correctSchedule2 = new Schedule(2, graph);
        Processor p2 = correctSchedule2.getProcessors().get(0);
        correctSchedule2.addTask(a2, p2, correctSchedule2.getEarliestSchedulableTime(a2, p2));

        int hash1 = correctSchedule1.hashCode();
        int hash2 = correctSchedule1.hashCode();

        assertTrue(hash1 == hash2);
    }

}
