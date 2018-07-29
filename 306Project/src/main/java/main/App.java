package main;

import java.util.List;

import io.GraphLoader;
import io.Query;
import org.graphstream.graph.Graph;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Hello world!
 *
 */
public class App extends Application{
	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println("Started"); // FOR DEBUGGING ON CONSOLE
		//Loads the graph, code should be smoved where the algorithm will be processed
		Parameters params = getParameters();
		List<String> args = params.getRaw();
		GraphLoader loader = new GraphLoader();
		Graph load = loader.load(args.get(0)); // Assumes first argument is always dot file name
		System.out.println("Done"); // FOR DEBUGGING ON CONSOLE
		Platform.exit(); // Stops javafx app in console
	}

	/**
	 * Main method. Handles the args. CANNOT YET HANDLE FAULTY/TROLL INPUT
	 * @param args
	 */
	public static void main(String[] args) {
		//Sorting the first two arguments, the file name and the number of processors
		String fileName = args[0];
		int processorNumber = Integer.parseInt(args[1]);

		//Handling the options
		if (args.length == 2) {
			launch(args); //The only time we have  args is if the option is -v, in which case we launch
		}
		else if (args.length == 3) {
			Query.handle(fileName, processorNumber);
		}
		else if (args.length == 4 && args[2].equals("p")) {
			int coreNumber = Integer.parseInt(args[3]);
			Query.handle(fileName, processorNumber, coreNumber);
		}
		else if (args.length == 4 && args[2].equals("o")) {
			String outputName = args[3];
			Query.handle(fileName, processorNumber, outputName);
		}
		else {
			//doSomething();
		}
	}
    
}
