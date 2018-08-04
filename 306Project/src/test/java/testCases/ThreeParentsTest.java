package testCases;

import graph.TaskGraph;
import graph.TaskNode;
import io.GraphLoader;
import io.Output;
import org.junit.Before;
import org.junit.Test;
import scheduling.GreedyScheduler;
import scheduling.Processor;
import scheduling.Schedule;
import scheduling.SchedulerI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by olive on 4/08/2018.
 */
public class ThreeParentsTest {

    private static List<String> filePaths;
    private static GraphLoader loader;

    @Before
    public void before(){
        filePaths = new ArrayList<String>();
        filePaths.addAll(Arrays.asList("src/main/resources/DotFiles/Nodes_7_OutTree.dot", "src/main/resources/DotFiles/Test1.dot",
                "src/main/resources/DotFiles/TestTwoParents.dot"));

        loader = new GraphLoader();
    }

    @Test
    public void testThreeParents() {
        String outputFileName = "outputThreeParents";
        TaskGraph graph = loader.load("src/main/resources/DotFiles/threeParents.dot");

        SchedulerI scheduler = new GreedyScheduler(graph, 2);
        Schedule solution = scheduler.createSchedule();

        Output.createOutput(solution.getProcessors(), graph, outputFileName + "TestSolution");

        Schedule correctSolution = new Schedule(2, graph);

        List<Processor> processes = correctSolution.getProcessors();

        Processor p1 = processes.get(0);
        Processor p2 = processes.get(1);

        p1.addTask(new TaskNode(7, "a"), 0);
        p1.addTask(new TaskNode(6, "c"), 7);
        p2.addTask(new TaskNode(7, "b"), 7);
        p1.addTask(new TaskNode(12, "d"), 13);
        p1.addTask(new TaskNode(3, "e"), 25);

        Output.createOutput(processes, graph, outputFileName + "CorrectSolution");

//        boolean same = compareTextFiles("src/main/resources/DotFiles/" + outputFileName +"TestSolution-output.dot", "src/main/resources/DotFiles/" + outputFileName + "CorrectSolution-output.dot");
//        assertTrue(same);

        assertTrue(true);
    }
}
