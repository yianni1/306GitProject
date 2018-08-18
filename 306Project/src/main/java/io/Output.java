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
 * Cleaned by Dweep and Oliver
 */
public class Output {

    //store a file object
    private static File file;

    /**
     * Creates an Ouput based on the CompletedSolution , graph and the outputFileName which basically writes the
     * completed solution into a dotFile
     * @param completedSolution
     * @param graph
     * @param outputFileName
     */
    public static void createOutput(List<Processor> completedSolution, TaskGraph graph, String outputFileName) {

        //Creating the file
        String fileName = outputFileName;

        //creating the new file Object
        file = new File(fileName);


        try {

            //create a new file
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
            //catching and input and output exception
            ioex.printStackTrace();
        }
    }

}
