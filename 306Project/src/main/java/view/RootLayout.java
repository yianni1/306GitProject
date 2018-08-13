package view;

import com.jfoenix.effects.JFXDepthManager;
import graph.TaskGraph;
import graph.TaskNode;
import io.GraphLoader;
import io.Output;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
    private AnchorPane topPane;

    @FXML
    private AnchorPane bottomPane;

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
        JFXDepthManager.setDepth(topPane, -1);
        JFXDepthManager.setDepth(bottomPane, -1);
        JFXDepthManager.setDepth(chartPane, 5);

        stackedBarChart.setLegendVisible(false);
        stackedBarChart.setVisible(false);

    }


    public void update(Schedule schedule) {
        final Schedule test = schedule;
        Platform.runLater(new Runnable() {
            public void run() {
                stackedBarChart.getData().clear();

                // translates schedule to series to show on stacked bar chart
                for (Processor processor : test.getProcessors()) {
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

                //Shows any idle time as a transparent block
                for (Object series : stackedBarChart.getData()) {
                    for (Object data : ((XYChart.Series)series).getData()) {
                        if (((XYChart.Series) series).getName().equals("Idle time")) {
                            ((XYChart.Data) data).getNode().setStyle("-fx-bar-fill: transparent; -fx-border-width: 0px;");
                        } else {
//                    ((XYChart.Data) data).getNode().setStyle("-fx-bar-fill: grey; -fx-border-width: 1px;; fx-border-color: black");
                        }
                    }
                }
            }
        });


    }

    @FXML
    private void btnStartHandler(ActionEvent event) throws IOException {
        stackedBarChart.setVisible(true);
        startTask();

    }

    private void startTask() {
        Runnable task = new Runnable() {
            public void run() {
                runTask();
            }
        };

        Thread backgroundThread = new Thread(task);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    private void runTask() {
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
