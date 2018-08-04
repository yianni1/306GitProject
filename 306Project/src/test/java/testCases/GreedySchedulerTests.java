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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
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

        taskGraphs = new ArrayList<TaskGraph>();

        for (String filePath : filePaths) {
            //System.out.println(filePath + " added to testing List.");
            taskGraphs.add(loader.load(filePath));
        }


    }

    @Test
    public void testTwoEntryNodes() throws IOException {

        // Put a unique file name (doesn't have to be the name of the graph), and the graphs file path.
        String outputFileName = "TwoParents";
        TaskGraph graph = loader.load("src/main/resources/DotFiles/TestTwoParents.dot");
        Schedule solution = scheduler.createSchedule(graph, 2);

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

        // Put a unique file name (doesn't have to be the name of the graph), and the graphs file path.
        String outputFileName = "TwoParents";
        TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_7_OutTree.dot");
        Schedule solution = scheduler.createSchedule(graph, 2);

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
        assertTrue(same);
    }

    /**
     * Compares 2 text files
     * @param file1
     * @param file2
     * @return true if they're the same, false if not the same.
     * @throws IOException
     */
    public boolean compareTextFiles(String file1, String file2) throws IOException {
        boolean areEqual = true;

        int lineNum1 = 1;
        int lineNum2 = 2;

        BufferedReader reader1 = new BufferedReader(new FileReader(file1));
        String line1 = reader1.readLine();
        while (line1 != null) {
            //System.out.println("TextA: Reading line " + lineNum1);
            BufferedReader reader2 = new BufferedReader(new FileReader(file2));
            String line2 = reader2.readLine();
            lineNum2 = 1;
            while (line2 != null) {
             //   System.out.println("TextB: Reading line " + lineNum2);
                if(!line1.equals(line2)) {
                    line2 = reader2.readLine();
                    lineNum2++;
                    if(line2 == null) {
                        areEqual = false;
                        System.out.println("Line " + lineNum1 + " does not appear in the second file.");
                        break;
                    }
                } else {
                    System.out.println("Line " + lineNum1 + " appears in line " + lineNum2 + " of TextB.");
                    break;
                }
            }
            reader2.close();
            line1 = reader1.readLine();
            lineNum1++;

        }
        reader1.close();

        if(areEqual) {
            System.out.println("Two files have same content.");
            return true;
        }
        else {
            return false;
        }
    }
}
