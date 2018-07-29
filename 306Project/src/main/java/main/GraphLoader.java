package main;

import java.io.IOException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;


/**
 * Created by yianni on 28/07/2018.
 */
public class GraphLoader {

	
	/**
	 * load method to load graph from dot file into Graph object
	 * @param filepath to dot file
	 * @return graph representation of dot file
	 */
	public Graph load(String filePath) {
		
		Graph graph = new SingleGraph("graph"); // Creates graph
		FileSource fs = null;		
		
		// Loads graph from filepath
		try {
			fs = FileSourceFactory.sourceFor(filePath); 
			
			fs.addSink(graph);

			fs.readAll(filePath);
			
		} catch( IOException e) {
			
		} finally {
			fs.removeSink(graph);
		}
		return graph;
	}
	
}
