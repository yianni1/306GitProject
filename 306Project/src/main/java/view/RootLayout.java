package view;

import com.jfoenix.effects.JFXDepthManager;
import graph.TaskGraph;
import graph.TaskNode;
import io.GraphLoader;
import io.Output;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import main.App;
import scheduling.DFBnBScheduler;
import scheduling.Processor;
import scheduling.Schedule;
import scheduling.Scheduler;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;


public class RootLayout implements Initializable{

    /**this is the Root Layout controller for the visualisation which would involve multiple visualisation aspects\
     * -> Statistics for the graph as the algorithm traverses through the graph
     * -> Live Visualisation of the algorithm traversing through the solution tree
     * -> Zoom and drag properties
     * Probably need some sort of adapter class in order to adapt out own data structures to that of the graphstream datastructures
     */


    @FXML
    private AnchorPane rootPane;

    @FXML
    private AnchorPane chartPane;

    @FXML
    private StackedBarChart stackedBarChart;

    @FXML
    private CategoryAxis yAxis;

    @FXML
    private NumberAxis xAxis;

    private String fileName;

    private int processorNumber;

    private Service<Void> backgroundThread;



    public void initialize(URL url, ResourceBundle rb) {
        JFXDepthManager.setDepth(chartPane, 5);

        XYChart.Series series = new XYChart.Series();

    }


    public void update(Schedule schedule) {

        stackedBarChart.getData().clear();

        for (Processor processor : schedule.getProcessors()) {
            int time = 0;
            for (TaskNode task : processor.getTasks()) {
                XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
                series.setName("Task " + task.getName());
                if (time < task.getStartTime()) {
                    XYChart.Series<String, Number> idle = new XYChart.Series<String, Number>();
                    idle.getData().add(new XYChart.Data(task.getStartTime() - time, "Processor " + processor.getID()));
                    idle.setName("Idle time");
                    stackedBarChart.getData().add(idle);
                }
                series.getData().add(new XYChart.Data(task.getWeight(), "Processor " + processor.getID()));
                stackedBarChart.getData().add(series);
                time = task.getEndTime();

            }
        }

//        stackedBarChart.getData().clear();
//        XYChart.Series tasks = new XYChart.Series();
//        XYChart.Series idle = new XYChart.Series();
//        for (Processor processor : schedule.getProcessors()) {
//            int time = 0;
//            for (TaskNode task : processor.getTasks()) {
//                time = time + task.getWeight();
//                if (time < task.getStartTime()) {
//                    idle.getData().add(new XYChart.Data(task.getStartTime() - time, "idle"));
//                    stackedBarChart.getData().add(idle);
//
//                }
//                tasks.getData().add(new XYChart.Data(task.getWeight(), "processor " + processor.getID()));
//                stackedBarChart.getData().add(tasks);
//
//            }
//        }
    }

    @FXML
    private void btnStartHandler(ActionEvent event) throws IOException {
        try {
            String outputN = fileName.substring(0, fileName.length() - 4);

            GraphLoader loader = new GraphLoader(); //Loading the graph

            String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            File parent = new File(path);
            String parentPath = parent.getParent() + File.separator;

            TaskGraph graph = loader.load(parentPath + fileName);

            //Doing the algorithm
            Scheduler solution = new DFBnBScheduler(graph, processorNumber);
            ((DFBnBScheduler) solution).setScheduleListener(this);
            Schedule finalSolution = solution.createSchedule();

            //Transporting to output
            Output.createOutput(finalSolution.getProcessors(), graph, parentPath + outputN + "-output.dot");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setProcessorNumber(int processorNumber) {
        this.processorNumber = processorNumber;
    }


}
