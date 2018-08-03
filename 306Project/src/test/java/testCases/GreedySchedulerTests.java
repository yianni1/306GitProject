package testCases;

import graph.TaskGraph;
import io.GraphLoader;
import org.graphstream.graph.Graph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import scheduling.GreedyScheduler;
import scheduling.Schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GreedySchedulerTests {
    private static Graph graphStreamGraph;
    private static List<String> filePaths;
    private static List<TaskGraph> taskGraphs;
    private static GraphLoader loader;
    private static GreedyScheduler scheduler;
    @Before
    public void before(){
        filePaths = new ArrayList<String>();
        filePaths.addAll(Arrays.asList("src/main/resources/DotFiles/Nodes_7_OutTree.dot", "src/main/resources/DotFiles/Test1.dot",
                "src/main/resources/DotFiles/TestTwoParents.dot"));

        loader = new GraphLoader();
        scheduler = new GreedyScheduler();

        for (String filePath : filePaths) {
            System.out.println(filePath);
            taskGraphs.add(loader.load(filePath));
        }


    }

    @Test
    public void testTwoEntryNodes(){
        TaskGraph graph = taskGraphs.get(2);
        Schedule schedule = scheduler.createSchedule(graph, 2);

        assertEquals(1,1);
    }
}
