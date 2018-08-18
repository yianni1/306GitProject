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
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_7_OutTree.dot");

        String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        File parent = new File(path);
        String parentPath = parent.getParent() + "\\";

        int coreNum = 3;

        Scheduler schedule = new DFBnBMasterScheduler(graph, 2, coreNum);
        Schedule solution = schedule.createSchedule();

        assertEquals(28, solution.getBound());
    }

    @Test
    public void testNode8OptimalTwoProcesses() throws URISyntaxException {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_8_Random.dot");

        String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        File parent = new File(path);
        String parentPath = parent.getParent() + "\\";

        int coreNum = 3;

        Scheduler schedule = new DFBnBMasterScheduler(graph, 2, coreNum);
        Schedule solution = schedule.createSchedule();

        assertEquals(581, solution.getBound());
    }

    @Test
    public void testNode9OptimalTwoProcesses() throws URISyntaxException {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_9_SeriesParallel.dot");

        String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        File parent = new File(path);
        String parentPath = parent.getParent() + "\\";

        int coreNum = 3;

        Scheduler schedule = new DFBnBMasterScheduler(graph, 2, coreNum);
        Schedule solution = schedule.createSchedule();

        assertEquals(55, solution.getBound());
    }

    @Test
    public void testNode10OptimalTwoProcesses() throws URISyntaxException {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_10_Random.dot");

        String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        File parent = new File(path);
        String parentPath = parent.getParent() + "\\";

        int coreNum = 3;

        Scheduler schedule = new DFBnBMasterScheduler(graph, 2, coreNum);
        Schedule solution = schedule.createSchedule();

        assertEquals(50, solution.getBound());
    }

    @Test
    public void testNode7Optimal4Processes() throws URISyntaxException {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_7_OutTree.dot");

        String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        File parent = new File(path);
        String parentPath = parent.getParent() + "\\";

        int coreNum = 3;

        Scheduler schedule = new DFBnBMasterScheduler(graph, 4, coreNum);
        Schedule solution = schedule.createSchedule();

        assertEquals(22, solution.getBound());
    }


    @Test
    public void testNode8Optimal4Processes() throws URISyntaxException {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_8_Random.dot");

        String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        File parent = new File(path);
        String parentPath = parent.getParent() + "\\";

        int coreNum = 3;

        Scheduler schedule = new DFBnBMasterScheduler(graph, 4, coreNum);
        Schedule solution = schedule.createSchedule();

        assertEquals(581, solution.getBound());
    }

    @Test
    public void testNode9Optimal4Processes() throws URISyntaxException {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_9_SeriesParallel.dot");

        String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        File parent = new File(path);
        String parentPath = parent.getParent() + "\\";

        int coreNum = 3;

        Scheduler schedule = new DFBnBMasterScheduler(graph, 4, coreNum);
        Schedule solution = schedule.createSchedule();

        assertEquals(55, solution.getBound());
    }

    @Test
    public void testNode10Optimal4Processes() throws URISyntaxException {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_10_Random.dot");

        String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        File parent = new File(path);
        String parentPath = parent.getParent() + "\\";

        int coreNum = 3;

        Scheduler schedule = new DFBnBMasterScheduler(graph, 4, coreNum);
        Schedule solution = schedule.createSchedule();

        assertEquals(50, solution.getBound());
    }

    @Test
    public void testNode11OptimalTwoProcesses() throws URISyntaxException {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_11_OutTree.dot");

        String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        File parent = new File(path);
        String parentPath = parent.getParent() + "\\";

        int coreNum = 3;

        Scheduler schedule = new DFBnBMasterScheduler(graph, 2, coreNum);
        Schedule solution = schedule.createSchedule();

        assertEquals(350, solution.getBound());
    }

    @Test
    public void testNode11OptimalFourProcesses() throws URISyntaxException {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_11_OutTree.dot");

        String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        File parent = new File(path);
        String parentPath = parent.getParent() + "\\";

        int coreNum = 3;

        Scheduler schedule = new DFBnBMasterScheduler(graph, 4, coreNum);
        Schedule solution = schedule.createSchedule();

        assertEquals(227, solution.getBound());
    }

}
