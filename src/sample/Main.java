package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import sample.app.App;
import sample.gui.Controller;

public class Main extends Application {
    private App app; // application part of project
    private Controller controller; // link between view and model (app)

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gui/sample.fxml"));

        Parent root = fxmlLoader.load();

        this.controller = fxmlLoader.getController();
        this.app = new App("src/sample/app/data/tempanomaly_4x4grid.csv");

        controller.linkModelAndController(app);

        primaryStage.setTitle("GlobalWarming3D");
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
