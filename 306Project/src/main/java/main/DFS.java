package main;

import org.graphstream.graph.DepthFirstIterator;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * Created by Ray on 28/07/2018.
 */
public class DFS {
        Graph inputGraph;
        Node startNode;
        DepthFirstIterator it;

    /**
     * Method to initialised before using DFS
     * @param graph: the graph we wish to span
     *         node: the starting node
     */
        public void compute(Graph graph, Node node) {
            inputGraph = graph;
            startNode = node;

            if (!startNode.equals(null)) {
                it = new DepthFirstIterator(startNode);
            } else {
                it = new DepthFirstIterator(inputGraph.getNode(0));
            }

            while (it.hasNext()) {
                Node n = it.next();
                // Do something (visualise node or what ever we want). This iterates through the graph in DFS.
            }

        }
}
