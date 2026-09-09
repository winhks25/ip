package stewie.ui.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import stewie.model.TaskList;

/**
 * Represents the entry point for Stewie's JavaFX user interface.
 */
public class StewieApplication extends Application {

    /**
     * Creates and displays the Stewie application window.
     *
     * @param stage the primary application window
     */
    @Override
    public void start(Stage stage) {
        StewieGui gui = new StewieGui(new TaskList());
        Scene scene = new Scene(gui, 1180, 760);
        scene.getStylesheets().add(getClass().getResource("/stewie/ui/gui/instagram.css").toExternalForm());

        stage.setTitle("Stewie — your task studio");
        stage.setMinWidth(960);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments supplied to the application
     */
    public static void main(String[] args) {
        launch(args);
    }
}
