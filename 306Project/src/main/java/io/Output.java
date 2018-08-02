package io;

import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;
import scheduling.Processor;
import scheduling.SimpleScheduler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by olive on 1/08/2018.
 */
public class Output {

    private static final String FILEPATH = "src/main/resources/DotFiles/";
    private static File file;
    private static String outputFileName;

    /**
     * Creates the output file. For the first submission, we will be using
     * a simple scheduler which only has one processor.
     * Returns the output file.
     * @param completedSolution
     * @return
     */
    public static void createOutput(Processor completedSolution, TaskGraph graph) {

        //Creating the file
        String fileName = FILEPATH + outputFileName + ".dot";
        file = new File(fileName);
        try {
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            writer.write("digraph " + outputFileName + "Output" + " {\n");

            for (TaskNode node : completedSolution.getTasks()) {
                writer.write("  " + node.getName() + "  [Weight=" + node.getWeight() + ",Start="
                        + (node.getEndTime() - node.getWeight()) + ",Processor=1];\n");
            }

            for (TaskEdge edge : graph.getEdges()) {
                writer.write("  " + edge.getStartNode().getName() + "->" + edge.getEndNode().getName() + "  ");
                writer.write("[Weight=" + edge.getWeight() + "];\n");
            }

            writer.write("}");
            writer.close();
        }
        catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }

    public static void setOutputFileName(String outputFileName) {
        Output.outputFileName = outputFileName;
    }
}
