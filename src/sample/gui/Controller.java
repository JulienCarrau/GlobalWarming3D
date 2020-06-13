package sample.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import sample.app.App;
import sample.app.YearTempAnomaly;
import sample.gui.view2D.legend.Legend;
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
    private YearTempAnomaly currentYearTempAnomaly;
    private String dataSelectedView;
    private int currentYear;

    /**
     * Initialize everything that doesn't need an access to the model.
     * @param url URL.
     * @param resourceBundle ResourceBundle.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dataSelectedView = "quadrilateral"; // quadrilateral histogram
        currentYear = 1880;

        histogramButton.setOnAction(actionEvent -> {
            dataSelectedView = "histogram";
            showDataOnEarth();
        });
        quadrilateralButton.setOnAction(actionEvent -> {
            dataSelectedView = "quadrilateral";
            showDataOnEarth();
        });
    }

    /**
     * 2nd initializer but this time it has access to the model.
     * @param app Application model.
     */
    public void linkModelAndController(App app) {
        earth = new Earth(pane3D, app.getKnownLocations(), app.getGlobalMinAndMax()); // Functionality: Afficher un globe en 3D et permettre à l’utilisateur tourner autour grâce à la souris.
        new Legend(pane3D, app.getGlobalMinAndMax().get(0), app.getGlobalMinAndMax().get(1), earth.getColors());

        currentYearTempAnomaly = app.getYearTempAnomaly(currentYear);
        showDataOnEarth();
    }

    /**
     * Functionality: Permettre à l'utilisateur de choisir le mode de visualisation des anomalies de température (quadrilatère ou histogramme).
     * Show data over earth according to selected data visualization and current year.
     */
    private void showDataOnEarth() {
        switch (dataSelectedView) {
            case "quadrilateral":
                earth.addQuadrilateralFilterOverWorld(currentYearTempAnomaly);
                break;
            case "histogram":
                earth.addHistogramFilterOverWorld(currentYearTempAnomaly);
        }
    }
}
