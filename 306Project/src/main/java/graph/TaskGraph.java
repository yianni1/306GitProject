package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olive on 29/07/2018.
 */
public class TaskGraph {

    private List<TaskEdge> edges = new ArrayList<TaskEdge>(); //The edges of the graph

    /**
     * Adds an edge
     * @param edge
     */
    public void addEdge(TaskEdge edge) {
        this.edges.add(edge);
    }
}
