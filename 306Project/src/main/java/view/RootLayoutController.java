package view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;

import javax.print.DocFlavor;
import java.util.Arrays;

/**
 * Created by Dweep on 26/07/2018
 */
public class RootLayoutController {

        /**
         * this is the Root Layout controller for the visualisation which would involve multiple visualisation aspects\
         * -> Statistics for the graph as the algorithm traverses through the graph
         * -> Live Visualisation of the algorithm traversing through the solution tree
         * -> Zoom and drag properties
         * -> looking at the thread visualisation so that we show the scheduling , the optimal schedule , with the time elapsed and the amount of nodes
         * that have been searched.
         * Probably need some sort of adapter class in order to adapt out own data structures to that of the graphstream datastructures
         */

    /**
     * this is for the category based on the nodes we probably have to have some way to find the number of nodes
     */
    @FXML
    CategoryAxis xAxisNodes = new CategoryAxis();


    /**
     * For this we need to get a string array of all the processors so that they can be added on the y - axis for the graph
     */
    @FXML
    NumberAxis yAxisProccessors = new NumberAxis();

    @FXML
    public void  initialize() {

        /**
         * will probably need to get a list of the nodes and then convert that list into a list array which can be set
         */
        xAxisNodes.setCategories(FXCollections.<String>observableArrayList(Arrays.asList("A", "B", "C", "D")));
        xAxisNodes.setLabel("Nodes");
        yAxisProccessors.setLabel("Processors");

        StackedBarChart<String, Number> StackedBarChart = new StackedBarChart<String,Number>(xAxisNodes,yAxisProccessors);

        XYChart.Series<String, Number> series1 = new XYChart.Series<String, Number>();


        //add some data in the graph which needs to be updated with a listener in the
        //algorithim

        //add the getData method so that it updates the stacked barChart
    }



}
