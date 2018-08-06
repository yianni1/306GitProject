package testCases;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import org.junit.Before;
import org.junit.Test;

import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;
import io.GraphLoader;
import io.Output;
import main.App;
import scheduling.GreedyScheduler;
import scheduling.Processor;
import scheduling.Schedule;
import scheduling.Scheduler;

public class AllTests extends testCases.CompareOutput {

    //Test Variables
    private static List<String> filePaths;
    private static int processorNum;
    private static Graph graphStreamGraph;

    /**
     * Initialise the paths before the tests.
     */
    @Before
    public void initialisePaths() {

        processorNum = 2;

        //file paths for all the tests
        filePaths = new ArrayList<String>();
        filePaths.addAll(Arrays.asList("src/main/resources/DotFiles/Nodes_7_OutTree.dot", "src/main/resources/DotFiles/Test1.dot",
                "src/main/resources/DotFiles/TestTwoParents.dot", "src/main/resources/DotFiles/Nodes_10_Random.dot",
                "src/main/resources/DotFiles/Nodes_9_SeriesParallel.dot", "src/main/resources/DotFiles/Nodes_11_OutTree.dot",
                "src/main/resources/DotFiles/Nodes_8_Random.dot"));
    }

    /**
     *  Tests the greedy schedule on whether it gains a output based upon the greedy rule of weights.
     */
    @Test
    public void testGreedySchedule() {
        for (String filePath : filePaths) {
            greedySchedule(filePath);
        }
        //greedySchedule("src/main/resources/DotFiles/Test1.dot");
    }

    private void greedySchedule(String filePath) {

        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load(filePath);

        GreedyScheduler schedule = new GreedyScheduler(graph, processorNum);
        Schedule solution = schedule.createSchedule();

        int nodeNum = 0;
        for (Processor processor : solution.getProcessors()) {
            int totalWeight = 0;

            for (TaskNode node : processor.getTasks()) {

                for (TaskEdge edge : node.getIncomingEdges()) {
                    TaskNode parentNode = edge.getStartNode();
                    int parentStart = parentNode.getEndTime();
                    int nodeStart = node.getEndTime();

                    //System.out.println(parentNode.getName() + " " + parentStart + " " + node.getName() + " " + nodeStart);
                    //Checks that the current node is scheduled later than its parents
                    assertTrue(parentStart <= nodeStart);

                }
                nodeNum++;
            }

            //Checks each node is only schedule once, i.e. no duplicate nodes in any processor
            HashSet<TaskNode> set = new HashSet<TaskNode>(processor.getTasks());
            if (set.size() < processor.getTasks().size()) {
                fail();
            }

        }

    }

    /**
     * Tests a basic graph.
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testTest1Dot() throws IOException, URISyntaxException {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/main/resources/DotFiles/Test1.dot");

        String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        File parent = new File(path);
        String parentPath = parent.getParent() + "\\";

        GreedyScheduler schedule = new GreedyScheduler(graph, 2);
        Schedule solution = schedule.createSchedule();

        Output.createOutput(solution.getProcessors(), graph, parentPath + "greedySolution.dot");


        Schedule correctSolution = new Schedule(2, graph);
        List<Processor> processes = correctSolution.getProcessors();

        Processor p1 = processes.get(0);
        Processor p2 = processes.get(1);

        p1.addTask(new TaskNode(3, "a"), 0);
        p1.addTask(new TaskNode(4, "b"), 3);
        p2.addTask(new TaskNode(6, "c"), 4);
        p2.addTask(new TaskNode(3, "d"), 10);

        Output.createOutput(processes, graph, parentPath + "greedySolutionCorrectSolution.dot");

        boolean same = compareTextFiles(parentPath + "greedySolution.dot", parentPath + "greedySolutionCorrectSolution.dot");
        assertTrue(same);
    }

    /**
     * Tests a graph schedule with three processors.
     * Note that there are multiple 'correct' solutions when three processors are used, and this test tests for any of them.
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testTripleProcessorDot() throws IOException, URISyntaxException {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/main/resources/DotFiles/TripleProcessor.dot");

        String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        File parent = new File(path);
        String parentPath = parent.getParent() + "\\";

        Scheduler schedule = new GreedyScheduler(graph, 3);
        Schedule solution = schedule.createSchedule();

        Output.createOutput(solution.getProcessors(), graph, parentPath + "greedySolutionTripleProcessor.dot");


        Schedule correctSolution1 = new Schedule(3, graph);
        List<Processor> processes1 = correctSolution1.getProcessors();

        Processor p1 = processes1.get(0);
        Processor p2 = processes1.get(1);
        Processor p3 = processes1.get(2);

        p1.addTask(new TaskNode(2, "d"), 0);
        p2.addTask(new TaskNode(5, "a"), 0);
        p3.addTask(new TaskNode(8, "g"), 0);
        p2.addTask(new TaskNode(4, "b"), 5);
        p3.addTask(new TaskNode(6, "c"), 8);
        p3.addTask(new TaskNode(7, "f"), 14);
        p2.addTask(new TaskNode(4, "e"), 19);

        Output.createOutput(processes1, graph, parentPath + "CorrectTripleProcessorS1.dot");


        Schedule correctSolution2 = new Schedule(3, graph);
        List<Processor> processes2 = correctSolution2.getProcessors();

        Processor twop1 = processes2.get(0);
        Processor twop2 = processes2.get(1);
        Processor twop3 = processes2.get(2);

        twop1.addTask(new TaskNode(2, "d"), 0);
        twop2.addTask(new TaskNode(5, "a"), 0);
        twop3.addTask(new TaskNode(8, "g"), 0);
        twop2.addTask(new TaskNode(4, "b"), 5);
        twop3.addTask(new TaskNode(6, "c"), 8);
        twop2.addTask(new TaskNode(7, "f"), 16);
        twop3.addTask(new TaskNode(4, "e"), 14);

        Output.createOutput(processes2, graph, parentPath + "CorrectTripleProcessorS2.dot");


        Schedule correctSolution3 = new Schedule(3, graph);
        List<Processor> processes3 = correctSolution3.getProcessors();

        Processor threep1 = processes3.get(0);
        Processor threep2 = processes3.get(1);
        Processor threep3 = processes3.get(2);

        threep1.addTask(new TaskNode(2, "d"), 0);
        threep2.addTask(new TaskNode(5, "a"), 0);
        threep3.addTask(new TaskNode(8, "g"), 0);
        threep2.addTask(new TaskNode(4, "b"), 5);
        threep3.addTask(new TaskNode(6, "c"), 8);
        threep1.addTask(new TaskNode(7, "f"), 16);
        threep3.addTask(new TaskNode(4, "e"), 14);

        Output.createOutput(processes3, graph, parentPath + "CorrectTripleProcessorS3.dot");


        boolean s1 = compareTextFiles(parentPath + "CorrectTripleProcessorS1.dot", parentPath + "greedySolutionTripleProcessor.dot");
        boolean s2 = compareTextFiles(parentPath + "CorrectTripleProcessorS2.dot", parentPath + "greedySolutionTripleProcessor.dot");
        boolean s3 = compareTextFiles(parentPath + "CorrectTripleProcessorS3.dot", parentPath + "greedySolutionTripleProcessor.dot");

        if (s1) {
            assertTrue(s1);
        } else if (s2) {
            assertTrue(s2);
        } else if (s3) {
            assertTrue(s3);
        } else {
            fail();
        }

    }

    /**
     * Tests a graph with two entry nodes.
     *
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testTwoEntryNodes() throws IOException, URISyntaxException {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/main/resources/DotFiles/TestTwoParents.dot");

        String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        File parent = new File(path);
        String parentPath = parent.getParent() + "\\";

        Scheduler scheduler = new GreedyScheduler(graph, 2);
        Schedule solution = scheduler.createSchedule();

        Output.createOutput(solution.getProcessors(), graph, parentPath + "TwoEntryTestSolution.dot");

        Schedule correctSolution = new Schedule(2, graph);

        List<Processor> processes = correctSolution.getProcessors();

        Processor p1 = processes.get(0);
        Processor p2 = processes.get(1);

        p1.addTask(new TaskNode(7, "c"), 0);
        p1.addTask(new TaskNode(3, "e"), 7);
        p2.addTask(new TaskNode(100, "b"), 0);
        p2.addTask(new TaskNode(12, "d"), 100);
        p2.addTask(new TaskNode(13, "f"), 112);

        Output.createOutput(processes, graph, parentPath + "TwoEntryCorrectSolution.dot");

        boolean same = compareTextFiles(parentPath + "TwoEntryTestSolution.dot", parentPath + "TwoEntryCorrectSolution.dot");
        assertTrue(same);
    }

    /**
     * Tests a graph with three parents.
     *
     * @throws Exception
     */
    @Test
    public void testThreeParents() throws Exception {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load("src/main/resources/DotFiles/threeParents.dot");

        String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        File parent = new File(path);
        String parentPath = parent.getParent() + "\\";

        Scheduler scheduler = new GreedyScheduler(graph, 2);
        Schedule solution = scheduler.createSchedule();

        Output.createOutput(solution.getProcessors(), graph, parentPath + "ThreeParentsTestSolution.dot");

        Schedule correctSolution = new Schedule(2, graph);

        List<Processor> processes = correctSolution.getProcessors();

        Processor p1 = processes.get(0);
        Processor p2 = processes.get(1);

        p1.addTask(new TaskNode(7, "a"), 0);
        p1.addTask(new TaskNode(6, "c"), 7);
        p2.addTask(new TaskNode(7, "b"), 9);
        p1.addTask(new TaskNode(12, "d"), 13);
        p1.addTask(new TaskNode(3, "e"), 25);

        Output.createOutput(processes, graph, parentPath + "ThreeParentsCorrectSolution.dot");

        boolean same = compareTextFiles(parentPath + "ThreeParentsTestSolution.dot", parentPath + "ThreeParentsCorrectSolution.dot");
        System.out.println(same);
        assertTrue(same);

    }

    /**
     * Tests a basic graph with 7 nodes.
     *
     * @throws IOException
     */
    @Test
    public void testNodes7() throws IOException {

        // Put a unique file name (doesn't have to be the name of the graph), and the graphs file path.
		 	/*GraphLoader loader = new GraphLoader();
	        String outputFileName = "Nodes7";
	        TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_7_OutTree.dot");
	        Scheduler scheduler = new GreedyScheduler(graph, 2);
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

    /**
     * Tests the graphLoader.
     */
    @Test
    public void testGraphLoader() {
        for (String filePath : filePaths) {
            System.out.println(filePath);
            loadGraphStreamGraph(filePath);
            testGraphLoader(filePath);
        }
    }

    /**
     * Tests graph loader for a particular graph
     *
     * @param filePath
     */
    private void testGraphLoader(String filePath) {
        GraphLoader loader = new GraphLoader();
        TaskGraph graph = loader.load(filePath);

        //This block checks the node names and weights are the same
        HashSet<TaskNode> nodes = graph.getNodes();
        for (TaskNode taskNode : nodes) {
            boolean nodesTrue = false;
            boolean nodeWeight = false;

            for (Node graphStreamNode : graphStreamGraph) {
                //If GraphStream Graph node name equals taskNode name, node is correct
                if (graphStreamNode.toString().equals(taskNode.getName())) {
                    nodesTrue = true;
                }

                //If GraphStream weight is equal to TaskNode weight, nodeWeight is correct.
                double nod = Double.parseDouble(graphStreamNode.getAttribute("Weight").toString());
                int nodeWeightInt = (int) nod;
                if (taskNode.getWeight() == nodeWeightInt) {
                    nodeWeight = true;
                }
            }
            //node names and weights should be true for all iterations
            assertTrue(nodesTrue);
            assertTrue(nodeWeight);
        }

        HashSet<TaskEdge> edges = graph.getEdges();

        //This block checks the edges source and target nodes match the GraphStream graph
        //Also checks weights
        for (TaskEdge taskEdge : edges) {
            boolean sourceNodeFromEdges = false;
            boolean targetNodeFromEdges = false;
            boolean taskEdgeWeight = false;

            for (Edge edge : graphStreamGraph.getEdgeSet()) {
                Node source = edge.getSourceNode();
                Node target = edge.getTargetNode();

                //If sourceNode name of GraphStream edge equals name of taskEdge start node,
                //TaskGraph source node of TaskEdge is correct
                if (source.toString().equals(taskEdge.getStartNode().getName())) {
                    sourceNodeFromEdges = true;
                }

                //If targetNode name of GraphStream edge equals name of taskEdge end node,
                //TaskGraph target node of TaskEdge is correct
                if (target.toString().equals(taskEdge.getEndNode().getName())) {
                    targetNodeFromEdges = true;
                }

                //Ensures weights are the same for TaskEdge and edge
                double edgeWeight = Double.parseDouble(edge.getAttribute("Weight").toString());
                int edgeWeightInt = (int) edgeWeight;
                if (edgeWeightInt == taskEdge.getWeight()) {
                    taskEdgeWeight = true;
                }

            }
            //edge names and weights should be true for all iterations
            assertTrue(sourceNodeFromEdges);
            assertTrue(targetNodeFromEdges);
            assertTrue(taskEdgeWeight);

        }
    }


    private void loadGraphStreamGraph(String filePath) {
        Graph graph = new SingleGraph("graph"); // Creates graph
        FileSource fs = null;

        // Loads graph from filepath
        try {
            fs = FileSourceFactory.sourceFor(filePath);

            fs.addSink(graph);

            fs.readAll(filePath);

        } catch (IOException e) {

        } finally {
            fs.removeSink(graph);
        }
        graphStreamGraph = graph;
    }


}
