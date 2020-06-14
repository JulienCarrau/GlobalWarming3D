package sample.gui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import sample.app.App;
import sample.app.YearTempAnomaly;
import sample.gui.view2D.YearView;
import sample.gui.view2D.Legend;
import sample.gui.view3D.Earth;

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

    private App model;
    private Earth earth;
    private YearView yearView;
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
        dataSelectedView = "histogram"; // quadrilateral or histogram
        currentYear = 1880;
        yearView = new YearView(pane3D, currentYear);

        setupButtons();
        setupSlider();
    }

    /**
     * Initialize styles and listeners to buttons.
     */
    private void setupButtons() {
        Background invisible = new Background(new BackgroundFill(Color.TRANSPARENT, null, null));
        Background selected = new Background(new BackgroundFill(Color.LIGHTGRAY, null, null));
        Border whenMouseIsOver = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(1), new BorderWidths(1)));
        Border otherwise = new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, new CornerRadii(1), new BorderWidths(1)));

        histogramButton.setBackground(invisible);
        histogramButton.borderProperty().bind(Bindings.when(histogramButton.hoverProperty()).then(whenMouseIsOver).otherwise(otherwise));
        histogramButton.setGraphic(new ImageView(new Image("sample/gui/icons/histogram.png")));
        histogramButton.setOnAction(actionEvent -> {
            histogramButton.setBackground(selected);
            quadrilateralButton.setBackground(invisible);

            dataSelectedView = "histogram";
            showDataOnEarth();
        });

        quadrilateralButton.setBackground(invisible);
        quadrilateralButton.borderProperty().bind(Bindings.when(quadrilateralButton.hoverProperty()).then(whenMouseIsOver).otherwise(otherwise));
        quadrilateralButton.setGraphic(new ImageView(new Image("sample/gui/icons/quadrilateral.png")));
        quadrilateralButton.setOnAction(actionEvent -> {
            quadrilateralButton.setBackground(selected);
            histogramButton.setBackground(invisible);

            dataSelectedView = "quadrilateral";
            showDataOnEarth();
        });

        playPauseButton.setBackground(invisible);
        playPauseButton.borderProperty().bind(Bindings.when(playPauseButton.hoverProperty()).then(whenMouseIsOver).otherwise(otherwise));
        playPauseButton.setGraphic(new ImageView(new Image("sample/gui/icons/play.png")));
        playPauseButton.setOnAction(actionEvent ->
            playPauseButton.setGraphic(new ImageView(new Image("sample/gui/icons/pause.png")))
        );

        speedButton.setBackground(invisible);
        speedButton.borderProperty().bind(Bindings.when(speedButton.hoverProperty()).then(whenMouseIsOver).otherwise(otherwise));
        speedButton.setGraphic(new ImageView(new Image("sample/gui/icons/fast-forward.png")));
    }

    /**
     * Functionality: Permettre à l’utilisateur de choisir l’année affichée.
     * Initialize slider and its listeners.
     */
    private void setupSlider() {
        yearSlider.setShowTickLabels(true);

        yearSlider.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double n) {
                if (n == 0) return "1880";
                if (n == 25) return "1915";
                if (n == 50) return "1950";
                if (n == 75) return "1985";
                return "2020";
            }

            // Not usefull here
            @Override
            public Double fromString(String s) {
                return 0d;
            }
        });

        yearSlider.valueProperty().addListener((observableValue, number, t1) -> {
            currentYear = 1880 + (int) ((yearSlider.getValue() * 140) / 100); // 140 = 2020 - 1880
            yearView.setYear(currentYear);
            currentYearTempAnomaly = model.getYearTempAnomaly(currentYear);
            showDataOnEarth();
        });
    }

    /**
     * 2nd initializer but this time it has access to the model.
     * @param app Application model.
     */
    public void linkModelAndController(App app) {
        model = app;
        earth = new Earth(pane3D, model.getKnownLocations(), model.getGlobalMinAndMax()); // Functionality: Afficher un globe en 3D et permettre à l’utilisateur tourner autour grâce à la souris.
        new Legend(pane3D, model.getGlobalMinAndMax().get(0), model.getGlobalMinAndMax().get(1), earth.getColors());

        currentYearTempAnomaly = model.getYearTempAnomaly(currentYear);

        histogramButton.fire();
    }

    /**
     * Functionality: Permettre à l'utilisateur de choisir le mode de visualisation des anomalies de température (quadrilatère ou histogramme).
     * Show data over earth according to selected data visualization and current year.
     */
    private void showDataOnEarth() {
        switch (dataSelectedView) {
            case "quadrilateral":
                earth.updateQuadrilateralFilterOverWorld(currentYearTempAnomaly);
                break;
            case "histogram":
                earth.updateHistogramFilterOverWorld(currentYearTempAnomaly);
        }
    }
}
