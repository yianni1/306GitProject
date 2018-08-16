package io;

import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;
import scheduling.Processor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by olive on 1/08/2018.
 */
public class Output {
 
    private static File file;

    /**
     * Creates the output file. For the first submission, we will be using
     * a simple scheduler which only has one processor.
     * Returns the output file.
     * @param completedSolution
     * @return
     */
    public static void createOutput(List<Processor> completedSolution, TaskGraph graph, String outputFileName) {


        //Creating the file
        String fileName = outputFileName;
        file = new File(fileName);
        try {
            file.createNewFile();

            //Writing to the file
            FileWriter writer = new FileWriter(file);
            writer.write("digraph " + "\"output" + graph.getTitle() + "\" {\n"); //Writing the first line
            int count = 0; //The processor number

            //For EAch processor, we go through each of its nodes
            //Then print out the weight, start and processor number
            for (Processor processor : completedSolution) {
                count++;

                for (TaskNode node : processor.getTasks()) {
                    writer.write("  " + node.getName() + "  [Weight=" + node.getWeight() + ",Start="
                            + (node.getEndTime() - node.getWeight()) + ",Processor=" + count + "];\n");
                }
            }

            //We now print all the edges out
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

}
