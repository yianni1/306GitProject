package testCases;

import graph.TaskGraph;
import io.GraphLoader;
import main.App;
import org.junit.Test;
import scheduling.DFBnBScheduler;
import scheduling.Schedule;
import scheduling.Scheduler;
import scheduling.parallel.DFBnBMasterScheduler;

import java.io.File;
import java.net.URISyntaxException;

import static junit.framework.TestCase.assertEquals;

public class ParrallelMasterTest {

    @Test
    public void testNode7OptimalTwoProcesses() throws URISyntaxException {
//        GraphLoader loader = new GraphLoader();
//        TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_7_OutTree.dot");
//
//        String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
//        File parent = new File(path);
//        String parentPath = parent.getParent() + "\\";
//
//        int coreNum = 3;
//
//        Scheduler schedule = new DFBnBMasterScheduler(graph, 2, coreNum);
//        Schedule solution = schedule.createSchedule();
//
//        assertEquals(28, solution.getBound());
    }

}
