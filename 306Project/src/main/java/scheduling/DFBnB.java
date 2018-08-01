package scheduling;

import graph.TaskGraph;
import graph.TaskNode;
import org.graphstream.graph.DepthFirstIterator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import scala.Array;
import scala.xml.pull.ProducerConsumerIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 28/07/2018.
 */
public class DFBnB {

        private int upperBound;
        private int depth;
        private Processor currentProcessor;

        // Index of the children of the schedule.
        private List<Integer> nodeIndices = new ArrayList<Integer>();
        private List<Integer> processorIndices = new ArrayList<Integer>();

        private Schedule optimalSchedule;
        private Schedule schedule;
        private List<TaskNode> schedulableNodes;


        public void createSchedule(TaskGraph graph, int processors) {
            schedule = new Schedule(processors);
            schedule.initializeSchedulableNodes(graph);

            depth = 0;
            upperBound = 0;

            for(TaskNode n: graph.getNodes() ) {
                upperBound = upperBound + n.getWeight();
            }

            nodeIndices.add(depth, 0);
            processorIndices.add(depth, 0);

            while (depth >= 0) {
                schedulableNodes = schedule.getSchedulableNodes();
                if (schedulableNodes.size() < 1) {
                    depth--;
                }


                TaskNode currentTask = schedulableNodes.get(nodeIndices.get(depth));

                currentProcessor = schedule.getProcessors().get(processorIndices.get(depth));

                currentProcessor.addTask(currentTask, currentTask.getEarliestSchedulableTime(currentProcessor));







                depth++;





            }

        }



}
