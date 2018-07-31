package scheduling;

import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by olive on 29/07/2018.
 */
public class SolutionTree {

    private Map<Integer, Processor> processors = new HashMap<Integer, Processor>();

    /**
     * Constructor builds a solution tree, with all the solutions
     * @param startingNodes
     * @param processorNumber
     */
    public SolutionTree(List<TaskNode> startingNodes, int processorNumber) {

        //Creating the processors
        for (int i = 0; i < processorNumber; i++) {
            processors.put(i, new Processor(i));
        }


    }
}
