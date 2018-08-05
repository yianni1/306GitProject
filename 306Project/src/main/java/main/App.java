package main;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import graph.TaskGraph;
import io.GraphLoader;
import io.Output;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import javafx.application.Application;
import javafx.stage.Stage;
import scheduling.GreedyScheduler;
import scheduling.Schedule;
import scheduling.SchedulerI;

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

		System.out.println("Starting Algorithum"); // FOR DEBUGGING ON CONSOLE

		Parameters params = getParameters(); 
		int size = params.getRaw().size();
		String[] args = params.getRaw().toArray(new String[size]);

		//Command line options using Apache Commons CLI
		Options options = new Options();

		options.addOption("p", true, "Number of cores to use");
		options.addOption("v", false, "Use visualisation");
		options.addOption("o", true, "Output file name" );

		checkOptions(args);

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		if (args.length < 2) {
			System.out.println("Insufficient arguments. Please specify input file and number of processors.");
			System.exit(0);
		} 
		else {
			//required arguments
			String fileName = args[0];

			int processorNumber = 0;

			try{
				processorNumber = Integer.parseInt(args[1]);
			}
			catch (NumberFormatException ex) {
				System.out.println("Please enter a positive integer for the processor number");
				System.exit(0);
			}

			//default values for optional arguments
			int numCores = 1;
			//			String outputName = fileName + "-output.dot";


			if (cmd.hasOption("p")) {


				try{
					numCores = Integer.parseInt(cmd.getOptionValue("p"));
				}
				catch (NumberFormatException ex) {
					System.out.println("Please enter a positive integer for the core number");
					System.exit(0);
				}


			}
			if (cmd.hasOption("o")) {
				//Block for user specified opiton
				String sendToOutputClass = cmd.getOptionValue("o");

				GraphLoader loader = new GraphLoader(); //Loading the graph
				String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
				File parent = new File(path);
				String parentPath = parent.getParent() + "\\";

				TaskGraph graph = loader.load(parentPath + fileName);

				//Doing the algorithm
				GreedyScheduler solution = new GreedyScheduler(graph, processorNumber);
				Schedule finalSolution = solution.createSchedule();

				//Transporting to output
				Output.createOutput(finalSolution.getProcessors(), graph, parentPath + sendToOutputClass + ".dot");
			} 
			else {

				boolean inputOk = checkArgs(args);

				if (inputOk) {
					//Block for non specified option
					String outputN = fileName.substring(0, fileName.length() - 4);


					String sendToOutputClass = outputN;

					GraphLoader loader = new GraphLoader(); //Loading the graph

					String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
					File parent = new File(path);
					String parentPath = parent.getParent() + "\\";

					TaskGraph graph = loader.load(parentPath + fileName);

					//Doing the algorithm
					SchedulerI solution = new GreedyScheduler(graph, processorNumber);
					Schedule finalSolution = solution.createSchedule();

					//Transporting to output
					Output.createOutput(finalSolution.getProcessors(), graph, parentPath + sendToOutputClass + "-output.dot");
				}
				else {
					System.out.println("Incorrect argument format");
					System.exit(0);
				}


			}
			if (cmd.hasOption("v")) {
				//If visualisation is required, initialise root layout
				initRootLayout();
			}

			System.out.println("Scheduling on " + processorNumber + " processors using " + numCores + " cores.");

			System.out.println("Finished!"); // FOR DEBUGGING ON CONSOLE
			System.exit(0);
		}
	}

	/**
	 * Checks the args of the input to ensure it is correct when there is no -o selected
	 * @param args
	 * @return
	 */
	private boolean checkArgs(String[] args) {

		if (args.length > 2) {
			String thirdArg = args[2];

			if (thirdArg.equals("-p") || thirdArg.equals("-v")) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return true;
		}

	}

	/**
	 * Checks input args to ensure it is vaild
	 * @return
	 * @throws URISyntaxException 
	 */
	private void checkOptions(String[] args) throws URISyntaxException {

		int vCount = 0;
		int oCount = 0;
		int pCount = 0;
		
		//Checks if the filename exists
		if (args.length > 0) {

			String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
			File parent = new File(path);
			String parentPath = parent.getParent() + "\\";

			File tmpDir = new File(parentPath + args[0]);
			boolean exists = tmpDir.exists();
			if (exists == false) {
				System.out.println("File name does not exist in current directory where jar is located");
				System.exit(0);
			}
		}
		
		if (args.length > 1) {
			int processorNumber = 0;

			try{
				processorNumber = Integer.parseInt(args[1]);
			}
			catch (NumberFormatException ex) {
				System.out.println("Please enter a positive integer for the processor number");
				System.exit(0);
			}
		}
		

		for (int i = 0; i < args.length; i++) {

			//Checks all arguments for options (indicated by -)
			if (args[i].charAt(0) == '-') {
				//Ensures the initial arguments, filename and processor num are not options
				if (args.length < 3) {
					System.out.println("Please enter a filename and or processor number without - at the front");
					System.exit(0);
				}
				//Ensures the option length is 2 for -o, -p, -v
				if (args[i].length() != 2) {
					System.out.println("Please enter a valid option");
					System.exit(0);
				}

				//Ensures the options are either -o, -p or -v
				if ((args[i].charAt(1) == 'o') || (args[i].charAt(1) == 'p') || args[i].charAt(1) == 'v') {
					
					//Counts the numebr of -o and -p args
					if (args[i].charAt(1) == 'o') {
						oCount++;
					}
					
					if (args[i].charAt(1) == 'p') {
						pCount++;
					}
					
					//If -v, next argument should be an option or nothing
					if (args[i].charAt(1) == 'v') {
						vCount++;
						if (i + 1 > args.length - 1) {
						}
						else if (args[i + 1].charAt(0) != '-') {
							System.out.println("Please enter a valid input for the option(s)");
							System.exit(0);
						}

					}
					//if -p or -o, next arg should be an input name or number not another option or nothing
					else if (i + 1 > args.length - 1) {
						System.out.println("Please enter a valid input for the option(s)");
						System.exit(0);
					}
					else if (args[i + 1].charAt(0) == '-') {
						System.out.println("Please enter a valid input for the option(s)");
						System.exit(0);

					}
					//Checks for additional unwanted args in input
					else if (i + 2 < args.length) {
						if (args[i + 2].charAt(0) != '-') {
							System.out.println("Superfluous arguments");
							System.exit(0);
						}
					}
					//EveryThing OK if passes this if statement
				}
				else {
					System.out.println("Please enter a valid option");
					System.exit(0);
				}
			}
			
			//Throws errors if repeated options in input
			if (vCount > 1) {
				System.out.println("Repeated -v options");
				System.exit(0);
			}
			else if (oCount > 1) {
				System.out.println("Repeated -o options");
				System.exit(0);
			}
			else if (pCount > 1) {
				System.out.println("Repeated -p options");
				System.exit(0);
			}
			
			//Checks for non option argument after processor number input
			if (args.length > 2) {
				if (args[2].charAt(0) != '-') {
					System.out.println("Superfluous arguments");
					System.exit(0);
				}
			}
			
		}
		

	}


	/**
	 * Main method. Handles the args
	 * @param args
	 */
	public static void main(String[] args) {

		//setting the GraphViewer to the advanced viewer
		System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		launch(args);
	}

	/**
	 * Initializes the root layout for the pane.
	 */
	private void initRootLayout() {
		try {
			//Load the root layout from the fxml file

			//printing out the location of the file
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/RootLayout.fxml"));

			//scene showing the root layout is displayed
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}