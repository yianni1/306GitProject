package main;

import java.io.IOException;
import java.util.List;

import io.GraphLoader;
import io.Query;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.graphstream.graph.Graph;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Hello world!
 *
 */
public class App extends Application{

	//private variables for the panes
	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Graph view");

		System.out.println("Started"); // FOR DEBUGGING ON CONSOLE
		//Loads the graph, code should be smoved where the algorithm will be processed

		Parameters params = getParameters();
		int size = params.getRaw().size();
		String[] args = params.getRaw().toArray(new String[size]);
//		List<String> args = params.getRaw();

		


		//Sorting the first two arguments, the file name and the number of processors
		String fileName = args[0];
		int processorNumber = Integer.parseInt(args[1]);

        Options options = new Options();

        options.addOption("p", true, "Number of cores to use");
        options.addOption("v", false, "Use visualisation");
        options.addOption("o", true, "Output file name" );

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (args.length < 2) {
            System.out.println("Please specify input file and number of processors");
        } else {
            Query.handle(fileName, processorNumber);
        }

        if (cmd.hasOption("v")) {
            initRootLayout();
        }


		//Handling the options
//		if (args.size() == 2) {
//            Query.handle(fileName, processorNumber);
//		}
//		else if (args.size() == 3 && args.get(2).equals("-v")) {
//            initRootLayout(); //The only time we have  args is if the option is -v, in which case we launch
//		}
//		else if (args.size() == 4 && args.get(2).equals("-p")) {
//			int coreNumber = Integer.parseInt(args.get(3));
//			Query.handle(fileName, processorNumber, coreNumber);
//		}
//		else if (args.size() == 4 && args.get(2).equals("-o")) {
//			String outputName = args.get(3);
//			Query.handle(fileName, processorNumber, outputName);
//		}
//		else {
//			//doSomething();
//		}



		GraphLoader loader = new GraphLoader();
		Graph load = loader.load(fileName); // Assumes first argument is always dot file name
		System.out.println("Done"); // FOR DEBUGGING ON CONSOLE
		Platform.exit(); // Stops javafx app in console
	}

	/**
	 * Main method. Handles the args. CANNOT YET HANDLE FAULTY/TROLL INPUT
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Initializes the root layout for the pane.
	 */
	private void initRootLayout() {
		try {
			//Load the root layout from the fxml file

			//printing out the location of the file
			Parent root =FXMLLoader.load(getClass().getResource("/fxml/RootLayout.fxml"));

			//scene showing the root layout is displayed
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
    
}
