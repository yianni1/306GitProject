package comparators;

import graph.TaskNode;

import java.util.Comparator;

public class CostFunctionComparator implements Comparator<TaskNode> {

    @Override
    public int compare(TaskNode node1, TaskNode node2) {

        int n1 = node1.getCostFunction();
        int n2 = node2.getCostFunction();
        if (n1 < n2) {
            // If n1 has a cheaper cost, return -1
            return -1;

        } else if (n1==n2){
            // If they're the same, sort alphabetically.
            return node1.getName().compareTo(node2.getName());

        } else {
            // If n2 has a cheaper cost, return 1
            return 1;

        }
    }


}
