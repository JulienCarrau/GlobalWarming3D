package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.app.Application;

public class Main { /* extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("GlobalWarming3D");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }*/


    public static void main(String[] args) {
        Application app = new Application("src/sample/app/data/tempanomaly_4x4grid.csv");
        System.out.println(app.getYearTempAnomaly(1930));
        //launch(args);
    }
}
