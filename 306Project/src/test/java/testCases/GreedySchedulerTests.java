package testCases;

import graph.TaskGraph;
import graph.TaskNode;
import io.GraphLoader;
import io.Output;
import org.graphstream.graph.Graph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import scheduling.GreedyScheduler;
import scheduling.Processor;
import scheduling.Schedule;
import scheduling.SchedulerI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class GreedySchedulerTests extends testCases.Test {
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
     
    }

    @Test
    public void testTwoEntryNodes() throws IOException {

        // Put a unique file name (doesn't have to be the name of the graph), and the graphs file path.
        String outputFileName = "TwoParents";
        TaskGraph graph = loader.load("src/main/resources/DotFiles/TestTwoParents.dot");
        
        SchedulerI scheduler = new GreedyScheduler(graph, 2);
        Schedule solution = scheduler.createSchedule();

        Output.createOutput(solution.getProcessors(), graph, outputFileName + "TestSolution");

        Schedule correctSolution = new Schedule(2, graph);

        List<Processor> processes = correctSolution.getProcessors();

        Processor p1 = processes.get(0);
        Processor p2 = processes.get(1);

        p1.addTask(new TaskNode(7, "c"), 0);
        p1.addTask(new TaskNode(3, "e"), 7);
        p2.addTask(new TaskNode(100, "b"), 0);
        p2.addTask(new TaskNode(12, "d"), 100);
        p2.addTask(new TaskNode(13, "f"), 112);

        Output.createOutput(processes, graph, outputFileName + "CorrectSolution");

        boolean same = compareTextFiles("src/main/resources/DotFiles/" + outputFileName +"TestSolution-output.dot", "src/main/resources/DotFiles/" + outputFileName + "CorrectSolution-output.dot");
        assertTrue(same);
    }

    @Test
    public void testNodes7() throws IOException {
/*
        // Put a unique file name (doesn't have to be the name of the graph), and the graphs file path.
        String outputFileName = "TwoParents";
        TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_7_OutTree.dot");
        SchedulerI scheduler = new GreedyScheduler(graph, 2);
        Schedule solution = scheduler.createSchedule();

        Output.createOutput(solution.getProcessors(), graph, outputFileName + "TestSolution");

        Schedule correctSolution = new Schedule(2, graph);

        List<Processor> processes = correctSolution.getProcessors();

        Processor p1 = processes.get(0);
        Processor p2 = processes.get(1);

        p1.addTask(new TaskNode(5, "0"), 0);
        p1.addTask(new TaskNode(5, "2"), 5);
        p1.addTask(new TaskNode(6, "3"), 10);
        p1.addTask(new TaskNode(6, "1"), 16);
        p1.addTask(new TaskNode(4, "4"), 22);
        p1.addTask(new TaskNode(7, "5"), 26);
        p1.addTask(new TaskNode(7, "6"), 33);

        Output.createOutput(processes, graph, outputFileName + "CorrectSolution");

        boolean same = compareTextFiles("src/main/resources/DotFiles/" + outputFileName +"TestSolution-output.dot", "src/main/resources/DotFiles/" + outputFileName + "CorrectSolution-output.dot");
        assertTrue(same);*/
    }


}
