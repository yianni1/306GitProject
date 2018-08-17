package comparators;

import graph.TaskNode;

import java.util.Comparator;

public class CostFunctionComparator implements Comparator<TaskNode> {

    @Override
    public int compare(TaskNode node1, TaskNode node2) {

        if (node1.getCostFunction() < node2.getCostFunction()) {
            return 1;
        }
        else {
            return 0;
        }
    }


}
