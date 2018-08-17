package testCases;

import graph.TaskGraph;
import graph.TaskNode;
import io.GraphLoader;
import org.junit.Test;
import scheduling.Processor;
import scheduling.Schedule;
import scheduling.parallel.DFBnBMasterScheduler;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class ParrallelUnitTests {

    @Test
    public void testinitialisePartialSchedules() {
//        GraphLoader loader = new GraphLoader();
//        TaskGraph graph = loader.load("src/main/resources/DotFiles/CustomTest.dot");
//
//        int numCores = 3;
//        int numProcesses = 2;
//
//
//        DFBnBMasterScheduler parrallelScheduler = new DFBnBMasterScheduler(graph, numProcesses, numCores);
//
//        parrallelScheduler.initialisePartialSchedules();
//        List<Schedule> scheduleList = parrallelScheduler.getPartialSchedules();
//
//        TaskNode a = null;
//        TaskNode b = null;
//        try {
//            for (TaskNode node : graph.getNodes()) {
//                if (node.getName().equals("a")) {
//                    a = (TaskNode) node.clone();
//                } else if (node.getName().equals("b")) {
//                    b = (TaskNode) node.clone();
//                }
//
//                if ((a != null) && (b != null)) {
//                    break;
//                }
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Schedule correctSchedule1 = new Schedule(numProcesses, graph);
//        Processor p1 = correctSchedule1.getProcessors().get(0);
//        correctSchedule1.addTask(a, p1, correctSchedule1.getEarliestSchedulableTime(a, p1));
//
//        //Schedule correctSchedule2 = new Schedule(numProcesses, graph);
//        //Processor p2 = correctSchedule2.getProcessors().get(1);
//        //correctSchedule2.addTask(a, p2, correctSchedule2.getEarliestSchedulableTime(a, p2));
//
//        Schedule correctSchedule3 = new Schedule(numProcesses, graph);
//        Processor p3 = correctSchedule3.getProcessors().get(0);
//        correctSchedule3.addTask(b, p3, correctSchedule3.getEarliestSchedulableTime(b, p3));
//
//        boolean correct1 = false;
//       // boolean correct2 = false;
//        boolean correct3 = false;
//        for (Schedule s : scheduleList) {
//            if (s.equals(correctSchedule1)) {
//                correct1 = true;
//            }
//        //    else if (s.equals(correctSchedule2)) {
//         //       correct2 = true;
//        //   }
//            else if (s.equals(correctSchedule3)) {
//                correct3 = true;
//            }
//        }
//
//        assertTrue(correct1);
//     //   assertTrue(correct2);
//        assertTrue(correct3);
    }


}
