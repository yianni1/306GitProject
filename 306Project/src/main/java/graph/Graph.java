package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olive on 29/07/2018.
 */
public class Graph {

    private List<Edge> edges = new ArrayList<Edge>(); //The edges of the graph

    /**
     * Adds an edge
     * @param edge
     */
    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }
}
