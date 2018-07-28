package main;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Hello world!
 *
 */
public class App extends Application{
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		 System.out.println( "Hello World!" );
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
			Query.handle(fileName, processorNumber);
		}
		else if (args.length == 3) {
			launch(args); //The only time we have 3 args is if the option is -v, in which case we launch
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
