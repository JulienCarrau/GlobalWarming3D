package sample.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import sample.app.LatLonPair;
import sample.app.YearTempAnomaly;
import sample.gui.view3D.Earth;

import java.net.URL;
import java.util.ArrayList;
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
    private int currentYear;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentYear = 1980;
        earth = new Earth(pane3D);
    }

    /**
     * Draw a quadrilateral map over the earth showing temperatures anomaly.
     * @param locations List of known locations.
     * @param anomaly Temperature anomaly of this year.
     */
    public void setQuadrilateralFliter(ArrayList<LatLonPair> locations, YearTempAnomaly anomaly) {
        earth.addQuadrilateralFilterOverWorld(locations, anomaly);
    }
}
