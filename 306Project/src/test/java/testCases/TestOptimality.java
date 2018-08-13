package testCases;

import graph.TaskGraph;
import io.GraphLoader;
import org.junit.Before;
import org.junit.Test;
import scala.util.parsing.combinator.testing.Str;
import scheduling.DFBnBScheduler;
import scheduling.Schedule;
import scheduling.Scheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Created by olive on 13/08/2018.
 */
public class TestOptimality {

    private List<File> files = new ArrayList<File>();

    @Before
    public void setUp() {
        File folder = new File("src/main/resources/DotFiles/");
        File[] paths = folder.listFiles();
        for (File file : paths) {
            if (file.getName().startsWith("2p") ||
                    file.getName().startsWith("4p") ||
                    file.getName().startsWith("8p")) {
                files.add(file);
            }

        }
    }

    @Test
    public void testOptimality() throws Exception {

        for (File file : files) {
            System.out.println(file.getName());
            //Loading the file
            GraphLoader loader = new GraphLoader();
            TaskGraph graph = loader.load("src/main/resources/DotFiles/" + file.getName());
            Scheduler schedule = new DFBnBScheduler(graph, 2);
            Schedule solution = schedule.createSchedule();
            System.out.println("Algorithm done");

            //Finding the optimal
            int optimal = 0;
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                //Finding the line with the optimal
                String line = scanner.nextLine();
                if (line.contains("Total schedule length")) {
                    String number = line.replaceAll("[^0-9]", "");
                    optimal = Integer.parseInt(number);
                }
            }
            assertEquals(solution.getBound(), optimal);
            System.out.println("passed");
        }
    }
}
