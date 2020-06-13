package sample.gui;

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
    private Legend legend;
    private String dataSelectedView;
    private int currentYear;

    /**
     * Initialize everything that doesn't need an access to the model.
     * @param url URL.
     * @param resourceBundle ResourceBundle.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dataSelectedView = "quadrilateral";
        currentYear = 1880;
    }

    /**
     * 2nd initializer but this time it has access to the model.
     * @param app Application model.
     */
    public void linkModelAndController(App app) {
        earth = new Earth(pane3D, app.getKnownLocations(), app.getGlobalMinAndMax()); // Fonctionality: Afficher un globe en 3D et permettre à l’utilisateur tourner autour grâce à la souris.
        legend = new Legend(pane3D, app.getGlobalMinAndMax().get(0), app.getGlobalMinAndMax().get(1), earth.getColors());

        showDataOnEarth(app.getYearTempAnomaly(currentYear));
    }

    /**
     * Show data over earth according to selected data visualization.
     * @param yta YearTempAnomaly corresponding to data to show.
     */
    private void showDataOnEarth(YearTempAnomaly yta) {
        switch (dataSelectedView) {
            case "quadrilateral":
                earth.addQuadrilateralFilterOverWorld(yta);
                break;
            case "histogram":

        }
    }
}
