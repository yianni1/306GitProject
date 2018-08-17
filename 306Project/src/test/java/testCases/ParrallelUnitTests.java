package testCases;

import graph.TaskGraph;
import graph.TaskNode;
import io.GraphLoader;
import org.junit.Test;
import scheduling.*;
import scheduling.parallel.DFBnBMasterScheduler;
import scheduling.parallel.DFBnBSlaveScheduler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class ParrallelUnitTests {

    @Test
    public void testinitialisePartialSchedules() {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/main/resources/DotFiles/CustomTest.dot");

        int cores = 3;
        int processors = 2;

        DFBnBMasterScheduler scheduler = new DFBnBMasterScheduler(graph, processors, cores);
        scheduler.initialisePartialSchedules();

        List<Schedule> scheduleList =  scheduler.getPartialSchedules();

        TaskNode a1 = null;
        TaskNode a2 = null;
        TaskNode b1 = null;
        TaskNode b2 = null;
        TaskNode g1 = null;
        TaskNode g2 = null;

        try {
            for (TaskNode node : graph.getNodes()) {
                if (node.getName().equals("a")) {
                    a1 = (TaskNode) deepClone(node);
                    a2 = (TaskNode) deepClone(node);
                } else if (node.getName().equals("b")) {
                    b1 = (TaskNode) deepClone(node);
                    b2 = (TaskNode) deepClone(node);
                }
                else if (node.getName().equals("g")) {
                    g1 = (TaskNode) deepClone(node);
                    g2 = (TaskNode) deepClone(node);
                }

                if ((a1 != null) && (b1 != null) && (g1 != null)) {
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Schedule correctSchedule1 = new Schedule(processors, graph);
        Processor p1 = correctSchedule1.getProcessors().get(0);
        correctSchedule1.addTask(a1, p1, correctSchedule1.getEarliestSchedulableTime(a1, p1));

        Schedule correctSchedule2 = new Schedule(processors, graph);
        Processor p2 = correctSchedule2.getProcessors().get(1);
        correctSchedule2.addTask(a2, p2, correctSchedule2.getEarliestSchedulableTime(a2, p2));

        Schedule correctSchedule3 = new Schedule(processors, graph);
        Processor p3 = correctSchedule3.getProcessors().get(0);
        correctSchedule3.addTask(b1, p3, correctSchedule3.getEarliestSchedulableTime(b1, p3));

        Schedule correctSchedule4 = new Schedule(processors, graph);
        Processor p4 = correctSchedule4.getProcessors().get(1);
        correctSchedule3.addTask(b2, p4, correctSchedule4.getEarliestSchedulableTime(b2, p4));

        Schedule correctSchedule5 = new Schedule(processors, graph);
        Processor p5 = correctSchedule5.getProcessors().get(0);
        correctSchedule3.addTask(g1, p5, correctSchedule5.getEarliestSchedulableTime(g1, p5));

        Schedule correctSchedule6 = new Schedule(processors, graph);
        Processor p6 = correctSchedule6.getProcessors().get(1);
        correctSchedule3.addTask(g2, p6, correctSchedule6.getEarliestSchedulableTime(g2, p6));

        boolean correct1 = false;
        boolean correct2 = false;
        boolean correct3 = false;
        boolean correct4 = false;
        boolean correct5 = false;
        boolean correct6 = false;
        for (Schedule s : scheduleList) {
            if (correct1 != true) {
                correct1 = equals(s, correctSchedule1);
            }

            if (correct2 != true) {
                correct2 = equals(s, correctSchedule2);
            }

            if (correct3 != true) {
                correct3 = equals(s, correctSchedule3);
            }

            if (correct4 != true) {
                correct4 = equals(s, correctSchedule4);
            }

            if (correct5 != true) {
                correct5 = equals(s, correctSchedule5);
            }

            if (correct6 != true) {
                correct6 = equals(s, correctSchedule6);
            }

        }

        assertTrue(scheduleList.size() == 6);
        assertTrue(correct1);
        assertTrue(correct2);
        assertTrue(correct3);
        assertTrue(correct4);
        assertTrue(correct5);
        assertTrue(correct6);

    }

    /**
     * This method makes a "deep clone" of any object it is given.
     */
    private static Object deepClone(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean equals(Schedule s1, Schedule s2) {

        boolean same = false;
        for (Processor p : s1.getProcessors()) {
            for (Processor c : s2.getProcessors()) {
                if (p.getID() == c.getID()) {

                    for (TaskNode task : p.getTasks()) {
                        for (TaskNode otherTask : c.getTasks()) {
                            if ((task.getName().equals(otherTask.getName())) && (task.getStartTime() == otherTask.getStartTime()) ) {
                                same = true;
                            }
                            else {
                                same = false;
                                return false;
                            }
                        }
                    }
                }

            }

        }

        return same;
    }

}
