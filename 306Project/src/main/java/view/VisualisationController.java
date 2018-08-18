package view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.effects.JFXDepthManager;
import graph.TaskGraph;
import graph.TaskNode;
import io.GraphLoader;
import io.Output;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import main.App;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import scheduling.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Written by Kevin. Controls visualisation elements + running scheduler on a background thread.
 */
public class VisualisationController implements Initializable{


    @FXML
    private JFXButton btnStart;

    @FXML
    private Label lblTitle;

    @FXML
    private Label lblStart;

    @FXML
    private Label lblProgress;

    @FXML
    private Label lblBound;

    @FXML
    private Label lblTime;

    @FXML
    private Label lblNumPaths;

    @FXML
    private Label lblBranchesPruned;

    @FXML
    private AnchorPane graphPane;

    @FXML
    private AnchorPane chartPane;

    @FXML
    private AnchorPane startPane;

    @FXML
    private AnchorPane timePane;

    @FXML
    private AnchorPane statsPane;

    @FXML
    private AnchorPane numPathsPane;

    @FXML
    private AnchorPane branchesPrunedPane;

    @FXML
    private StackedBarChart stackedBarChart;

    private String fileName;

    private int processorNumber;

    private int coreNumber;

    private Timeline timeline;

    private int mins = 0;
    private int secs = 0;
    private int millis = 0;

    private Viewer viewer;


    public void initialize(URL url, ResourceBundle rb) {
        //JFoenix depth for material design effect
        JFXDepthManager.setDepth(chartPane, 2);
        JFXDepthManager.setDepth(graphPane, 2);
        JFXDepthManager.setDepth(startPane, 2);
        JFXDepthManager.setDepth(timePane, 2);
        JFXDepthManager.setDepth(statsPane, 2);
        JFXDepthManager.setDepth(numPathsPane, 2);
        JFXDepthManager.setDepth(branchesPrunedPane, 2);

        //set panes opacity
        chartPane.setOpacity(0.7);
        graphPane.setOpacity(0.6);

        stackedBarChart.setAnimated(false);
        stackedBarChart.setLegendVisible(false);

        //start timeline for time elapsed pane
        timeline = new Timeline(new KeyFrame(Duration.millis(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateTimer();
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(false);

    }

    /**
     * Creates input graph to be displayed.
     */
    public void createGraph() {
        //set title
        lblTitle.setText("Scheduling on " + processorNumber + " processor(s) with " + coreNumber + " core(s) - " + fileName);
        GraphLoader loader = new GraphLoader(); //Loading the graph

        String path = null;

        try {
            //get path
            path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();

            File parent = new File(path);
            String parentPath = parent.getParent() + File.separator;
            Graph graph = loader.loadGraph(parentPath + fileName);

            graph.addAttribute("ui.quality");
            graph.addAttribute("ui.antialias");

            //add node labels
            for (Node node : graph.getNodeSet()) {
                node.addAttribute("ui.label", node.toString() + " : " + node.getAttribute("Weight").toString());
            }

            //add edge labels
            for (Edge edge: graph.getEdgeSet()) {
                edge.addAttribute("ui.label", edge.getAttribute("Weight").toString());
            }

            //set graph style
            graph.addAttribute("ui.stylesheet",
                    "node { " +
                            "shape: box; " +
                            "stroke-mode: plain; " +
                            "size-mode: fit; " +
                            "text-mode: normal; " +
                            "text-background-mode: rounded-box; " +
                            "text-background-color: black; " +
                            "text-color: white; " +
                            "text-style: normal; " +
                            "text-alignment: center; " +
                            "text-padding: 3px; " +
                            "text-size: 12px; " +
                            " }" +
                            "edge { " +
                            "arrow-size: 20px, 5px; " +
                            "text-alignment: under; " +
                            "text-size: 12px; " +
                            " }");

            // initialise GraphStream graph on a Swing thread
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
                    viewer.enableAutoLayout();
                    JPanel view = viewer.addDefaultView(false);
                    view.setPreferredSize(new Dimension(((Double) graphPane.getWidth()).intValue(), ((Double) graphPane.getHeight()).intValue()));

                    javafx.application.Platform.runLater(new Runnable() {
                        @Override public void run() {
                            SwingNode swingNode = new SwingNode();
                            swingNode.setContent(view);
                            graphPane.getChildren().add(swingNode);
                            graphPane.requestLayout();
                        }
                    });

                }
            });

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }





    }

    /**
     * Closes the GraphStream viewer
     */
    public void closeViewer() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                viewer.close();
            }
        });
    }

    /**
     * Updates the timer
     */
    private void updateTimer() {
        if(millis == 1000) {
            secs++;
            millis = 0;
        }
        if (secs == 60) {
            mins++;
            secs = 0;
        }
        lblTime.setText((((mins/10) == 0) ? "0" : "") + mins + ":"
                + (((secs/10) == 0) ? "0" : "") + secs + ":"
                + (((millis/10) == 0) ? "00" : (((millis/100) == 0) ? "0" : "")) + millis++);

    }

    /**
     * Updates number of paths. Called from scheduler.
     * @param numPaths the number of paths explored
     */
    public void updateNumPaths(long numPaths) {
            Platform.runLater(new Runnable() {
                public void run() {
                    lblNumPaths.setText("" + numPaths);
                }
            });
    }

    /**
     * Updates branches pruned. Called from scheduler.
     * @param branchesPruned the number of branches pruned
     */
    public void updateBranchesPruned(long branchesPruned) {
        Platform.runLater(new Runnable() {
            public void run() {
                lblBranchesPruned.setText("" + branchesPruned);
            }
        });
    }

    /**
     * Updates GUI state to done. Called from scheduler.
     */
    public void finish() {
        Platform.runLater(new Runnable() {
           public void run() {
               lblStart.setText("Done!");
               stackedBarChart.setTitle("Optimal Schedule");
               lblProgress.setText("click to restart");
               startPane.getStyleClass().clear();
               startPane.getStyleClass().add("start-pane");
               btnStart.setDisable(false);
               timeline.pause();
           }
        });
;
    }

    /**
     * Updates Gantt chart of the current best schedule.
     * @param schedule the current best schedule
     */
    public void updateSchedule(final Schedule schedule) {
        Platform.runLater(new Runnable() {
            public void run() {
                stackedBarChart.getData().clear();
                lblBound.setText("" + schedule.getBound());
                // translates schedule to series to show on stacked bar chart
                for (Processor processor : schedule.getProcessors()) {
                    int time = 0;
                    for (TaskNode task : processor.getTasks()) {
                        XYChart.Series series = new XYChart.Series<String, Number>();
                        series.setName(task.getName());
                        if (time < task.getStartTime()) {
                            XYChart.Series idle = new XYChart.Series<String, Number>();
                            idle.getData().add(new XYChart.Data(task.getStartTime() - time, "" + processor.getID()));
                            idle.setName("Idle time");
                            stackedBarChart.getData().add(idle);
                        }
                        series.getData().add(new XYChart.Data(task.getWeight(), "" + processor.getID()));
                        stackedBarChart.getData().add(series);
                        time = task.getEndTime();

                    }
                }

                //Shows any idle time as a transparent block
                for (final Object series : stackedBarChart.getData()) {
                    for (final Object data : ((XYChart.Series)series).getData()) {
                        if (((XYChart.Series) series).getName().equals("Idle time")) {
                            ((XYChart.Data) data).getNode().setStyle("-fx-bar-fill: transparent; -fx-border-width: 0px;");
                        } else {
                            ((XYChart.Data) data).getNode().setStyle("-fx-bar-fill: #CFD8DC; -fx-border-width: 1px; -fx-border-color: #607D8B;");
                            StackPane bar = (StackPane) ((XYChart.Data) data).getNode();
                            final Text dataText = new Text(((XYChart.Series) series).getName());
                            bar.getChildren().add(dataText);
                        }
                    }
                }
            }
        });


    }

    @FXML
    private void btnStartHandler(ActionEvent event) {
        btnStart.setDisable(true);
        stackedBarChart.getData().clear();
        stackedBarChart.setTitle("Current Best Schedule");
        lblBound.setText("0");
        lblNumPaths.setText("0");
        lblStart.setText("Running...");
        startPane.getStyleClass().clear();
        startPane.getStyleClass().add("start-pane-running");
        lblProgress.setText("scheduling in progress");
        lblTime.setText("00:00:000");
        lblBranchesPruned.setText("0");
        mins = 0;
        secs = 0;
        millis = 0;
        timeline.play();
        startTask();

    }

    /**
     * Creates a background thread and calls runTask().
     */
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

    /**
     * Starts the scheduler.
     */
    private void runTask() {
        try {
            String outputN = fileName.substring(0, fileName.length() - 4);

            GraphLoader loader = new GraphLoader(); //Loading the graph

            String path = (App.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            File parent = new File(path);
            String parentPath = parent.getParent() + File.separator;

            TaskGraph graph = loader.load(parentPath + fileName);

            Schedule finalSolution;

            //Doing the algorithm
            if (coreNumber > 1) {
                Scheduler solution = new DFBnBMasterScheduler(graph, processorNumber, coreNumber);
                ((DFBnBMasterScheduler) solution).setScheduleListener(this);
                finalSolution = solution.createSchedule();
            } else {
                Scheduler solution = new DFBnBScheduler(graph, processorNumber);
                ((DFBnBScheduler) solution).setScheduleListener(this);
                finalSolution = solution.createSchedule();
            }

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

    public void setCoreNumber(int coreNumber) {
        this.coreNumber = coreNumber;
    }


}
