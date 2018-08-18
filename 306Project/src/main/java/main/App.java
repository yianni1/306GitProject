package main;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import graph.TaskGraph;
import io.GraphLoader;
import io.Output;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.WindowEvent;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import javafx.application.Application;
import javafx.stage.Stage;
import scheduling.DFBnBMasterScheduler;
import scheduling.DFBnBScheduler;
import scheduling.Schedule;
import scheduling.Scheduler;
import view.VisualisationController;

/**
 * This is the main application, this is written so that it is compatible with Java Fx using the start method account
 * for the various kinds of inputs that can be parsed into this program which are the following:
 * -> -p N (Parallelization and the number of cores to be used on the system the program is run in)
 * -> -v (Visualisation of the algorithm that runs)
 * -> -o [Name] (this provides an output in the dot file format with the specificied name by the user)
 *
 */
public class App extends Application{

	//private variables for the panes
	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) throws Exception {

		//visualisation methods
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Task Scheduler - Team K.O.D.Y.R");

		Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Thin.ttf"), 12);
		Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Light.ttf"), 12);

		//For debugging at the console
		System.out.println("Starting Algorithm");


		Parameters params = getParameters(); 
		int size = params.getRaw().size();
		String[] args = params.getRaw().toArray(new String[size]);

		//Command line options using Apache Commons CLI
		Options options = new Options();

		//option for the parallelization of the core with an additional argument of how many cores are being used
		options.addOption("p", true, "Number of cores to use");

		//option for the visualisation of the graph
		options.addOption("v", false, "Use visualisation");

		//option for the custom output name provided to the user
		options.addOption("o", true, "Output file name" );

		checkOptions(args);

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		if (args.length < 2) {

			//checking if the argument len is less than 2 if so in this case it should not allow the program to run which catches
			//the exception and procedes to provide an error message
			System.out.println("Insufficient arguments. Please specify input file and number of processors.");
			System.exit(0);
		} 
		else {

			//required arguments which is filename and processor is number
			String fileName = args[0];


			int processorNumber = 0;

			try{

				//store the processor number as per the parsed argument
				processorNumber = Integer.parseInt(args[1]);
			}

			//catch an exception if the number for the processor is negative
			catch (NumberFormatException ex) {
				System.out.println("Please enter a positive integer for the processor number");
				System.exit(0);
			}

			//default values for optional arguments
			int numCores = 1;

			//this the parallelization option which takes in a parameter which is the number of cores that it
			//is on
			if (cmd.hasOption("p")) {


				try{

					//store the parse value as the num cores value
					numCores = Integer.parseInt(cmd.getOptionValue("p"));
				}

				//check if the integer that is added is positive and not negative
				catch (NumberFormatException ex) {
					System.out.println("Please enter a positive integer for the core number");
					System.exit(0);
				}

			}

			//this is the option for the visualisation which opens the GUI element of the algorithim
			if (cmd.hasOption("v")) {
				try {

					//Load the root layout from the fxml file
					FXMLLoader fxmlLoader = new FXMLLoader();
					Parent root = fxmlLoader.load(getClass().getResource("/view/Visualisation.fxml").openStream());

					VisualisationController controller = fxmlLoader.getController();

					controller.setFileName(fileName);
					controller.setProcessorNumber(processorNumber);
					controller.setCoreNumber(numCores);

					//if the option is o as well as the V then we can set the output name in the controller to be ouputted
					//properly when the output file is generated
					if (cmd.hasOption("o")) {
						controller.setOutputFileName(cmd.getOptionValue("o"));
					}

					primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
						@Override
						public void handle(WindowEvent t) {
							Platform.exit();
							System.exit(0);
							controller.closeViewer();
						}
					});

					//scene showing the root layout is displayed
					Scene scene = new Scene(root);
					primaryStage.setScene(scene);
					primaryStage.getIcons().add(new Image(App.class.getResourceAsStream("/background/icon.png")));
					primaryStage.setResizable(false);
					primaryStage.sizeToScene();
					controller.createGraph();
					primaryStage.show();

					//catching an input and output exception
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {

				//checking if the option is output which shall have an additional argument that determines the filesname
                if (cmd.hasOption("o")) {

                    //Block for the user specificed option
                    String sendToOutputClass = cmd.getOptionValue("o");

                    //loading the specific graph
                    GraphLoader loader = new GraphLoader();

                    //get the path of the graph file (Must be in the target folder)
                    String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();

                    File parent = new File(path);
                    String parentPath = parent.getParent() + File.separator;

                    //load the graph value
                    TaskGraph graph = loader.load(parentPath + fileName);

					Schedule finalSolution = null;
                    if (cmd.hasOption("p")) {
						//Doing the algorithm
						Scheduler solution = new DFBnBMasterScheduler(graph, processorNumber, numCores);
						finalSolution = solution.createSchedule();
					}
					else {
						//Doing the algorithm
						Scheduler solution = new DFBnBScheduler(graph, processorNumber);
						 finalSolution = solution.createSchedule();
					}

                    //Transporting to output. and output with either -output for a default output or the name of the output file
                    Output.createOutput(finalSolution.getProcessors(), graph, parentPath + sendToOutputClass + ".dot");

                    //exiting the system
					System.exit(0);
                }
                else {

                	//checking for other input arguments such as that of p and v
                    boolean inputOk = checkArgs(args);

                    if (inputOk) {

                    	//this is teh output defualt name which takes the name of the output and adds a -output to the
						//end of it
                        String outputN = fileName.substring(0, fileName.length() - 4);


                        String sendToOutputClass = outputN;

                        //loading the graph
                        GraphLoader loader = new GraphLoader();

                        //get the path of the location for the dot file
                        String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
                        File parent = new File(path);
                        String parentPath = parent.getParent() + File.separator;


                        TaskGraph graph = loader.load(parentPath + fileName);


						Schedule finalSolution = null;
						if (cmd.hasOption("p")) {
							//Doing the algorithm
							Scheduler solution = new DFBnBMasterScheduler(graph, processorNumber, numCores);
							finalSolution = solution.createSchedule();
						}
						else {
							//Doing the algorithm
							Scheduler solution = new DFBnBScheduler(graph, processorNumber);
							finalSolution = solution.createSchedule();
						}

                        //Transporting to output
                        Output.createOutput(finalSolution.getProcessors(), graph, parentPath + sendToOutputClass + "-output.dot");
						System.exit(0);
                    }
                    else {

                    	//if an incorrect argument format is obtained then the exit the system and notify the user
                        System.out.println("Incorrect argument format");
                        System.exit(0);
                    }


                }
            }


			//print statement to keep track for debugging
			System.out.println("Scheduling on " + processorNumber + " processors using " + numCores + " cores.");
		}
	}

	/**
	 * Checks the args of the input to ensure it is correct when there is no -o selected
	 * @param args
	 * @return boolean (True or False)
	 */
	private boolean checkArgs(String[] args) {

		if (args.length > 2) {
			String thirdArg = args[2];

			//checking if the third argument is either a -p or a -v
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
	 * Checks input arguments for validity and ensures statements are provided in those cases
	 * @return
	 * @throws URISyntaxException 
	 */
	private void checkOptions(String[] args) throws URISyntaxException {

		//the counter for the amount of times the particular option is called since there is no use for double ups
		int vCount = 0;
		int oCount = 0;
		int pCount = 0;
		
		//Checks if the filename exists, if it doesn't sends a validity statement
		if (args.length > 0) {

			//finding the location of the file
			String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
			File parent = new File(path);
			String parentPath = parent.getParent() + File.separator;


			File tmpDir = new File(parentPath + args[0]);

			//checking if the file exists
			boolean exists = tmpDir.exists();

			//if the file doesn't exist in that location send a user a message
			if (exists == false) {
				System.out.println("File name does not exist in current directory where jar is located");
				System.exit(0);
			}
		}

		//checking for the processor number
		if (args.length > 1) {
			int processorNumber = 0;

			try{
				processorNumber = Integer.parseInt(args[1]);
			}

			//checking if the integer which is passed is positive
			catch (NumberFormatException ex) {
				System.out.println("Please enter a positive integer for the processor number");

				//exit the system
				System.exit(0);
			}
		}
		
		//iterating through all the arguments that have been parsed
		for (int i = 0; i < args.length; i++) {

			//checking all of "-" type arguments and options
			if (args[i].charAt(0) == '-') {

				//Ensures that the intial arguments that have been provide such as the
				//-> File path is not an option argument
				//-> the number of processors to use
				if (args.length < 3) {
					System.out.println("Please enter a filename and or processor number without - at the front");
					System.exit(0);
				}
				//Ensuring that all the lengths for the options are 2 which involve -p , -v, -o, if that is not the case
				//a validity check is initiated
				if (args[i].length() != 2) {
					System.out.println("Please enter a valid option");

					//exiting the system
					System.exit(0);
				}

				//Ensures the options are either -o, -p or -v
				if ((args[i].charAt(1) == 'o') || (args[i].charAt(1) == 'p') || args[i].charAt(1) == 'v') {
					
					//Counts the numebr of -o args
					if (args[i].charAt(1) == 'o') {
						oCount++;
					}

					//Counts the number of the -p args
					if (args[i].charAt(1) == 'p') {
						pCount++;
					}
					
					//If -v, next argument should be an option or nothing
					if (args[i].charAt(1) == 'v') {
						vCount++;

						//chceking if the option argument has any other problems
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
	 * Main method. Handles the arguments which are parsed into command line
	 * @param args
	 */
	public static void main(String[] args) {

		//setting the GraphViewer to the advanced viewer
		System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		launch(args);
	}

}