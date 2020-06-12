package sample.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import sample.gui.objects.Earth;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Pane pane3D;

    @FXML
    private Button readUpBackWard, playPauseButton, speedButton, histogramButton, quadrilateralButton;

    @FXML
    private Label speedLabel;

    @FXML
    private Slider yearSlider;

    private Earth earth;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        earth = new Earth(pane3D);


    }

    public void test(String t) {
        readUpBackWard.setText(t);
    }
}
