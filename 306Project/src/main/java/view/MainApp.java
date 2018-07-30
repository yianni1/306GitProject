package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    //private variables for the panes
    private Stage primaryStage;
    private BorderPane rootLayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Graph view");

        initRootLayout();

    }

    /**
     * Initializes the root layout for the pane.
     */
    public void initRootLayout() {
        try {
            //Load the root layout from the fxml file

            //printing out the location of the file
            System.out.println(getClass().getResource("/view/RootLayout.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();

            //scene showing the root layout is displayed
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Returns the main stage
     * @return primaryStage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
