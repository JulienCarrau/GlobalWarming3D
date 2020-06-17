package sample.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.PickResult;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.StringConverter;
import sample.app.*;
import sample.gui.view3D.Earth;
import sample.gui.view2D.*;

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

    private App model; // Application model
    private int currentYear; // Current year
    private YearTempAnomaly currentYearTempAnomaly; // Temperature anomaly of current year

    private Earth earth; // Earth 3D representation
    private String dataSelectedView; // Selected data visualization (histogram / quadrilateral)

    private LatLonView latLonView; // Show lat lon mouse's position

    private YearView yearView; // Show year in pane

    private Timeline yearAnimation; // Animation
    private boolean animationActive, animationIsForward; // Keep track of when animation is played or pause
    private int speedRate; // Animation's speed rate

    private PopUpPlot popUpPlot; // Pop up window where plots are drawn
    private double mousePressedX, mousePressedY; // To check if mouse is clicked or just used to move earth (with CameraManager)
    private boolean popUpActive; // Kepp track if pop up is active or not

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
        latLonView = new LatLonView(pane3D);
        animationActive = false;
        animationIsForward = true;
        popUpActive = false;
        speedLabel.setText("x1");
        speedRate = 1;

        yearAnimation = new Timeline(new KeyFrame(Duration.seconds(1), actionEvent1 -> {
            goToNextYear();
            yearSlider.valueProperty().setValue(currentYear);
        }));
        yearAnimation.setCycleCount(Timeline.INDEFINITE);

        pane3D.setOnMouseEntered(mouseEvent -> pane3D.setCursor(Cursor.OPEN_HAND));
        pane3D.setOnMouseExited(mouseEvent -> pane3D.setCursor(Cursor.DEFAULT));

        setupButtons();
        setupSlider();
    }

    /**
     * Functionalities:
     *      - Animer l’évolution des anomalies de température année après année.
     *      - Permettre à l'utilisateur de mettre en pause, d’arrêter et de reprendre l’animation.
     *      - Permettre à l'utilisateur de régler la vitesse de l’animation.
     * Initialize styles and listeners to buttons.
     */
    private void setupButtons() {
        Background invisible = new Background(new BackgroundFill(Color.TRANSPARENT, null, null));
        Background selected = new Background(new BackgroundFill(Color.LIGHTGRAY, null, null));
        Border whenMouseIsOver = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(1), new BorderWidths(1)));
        Border otherwise = new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, new CornerRadii(1), new BorderWidths(1)));

        histogramButton.setTooltip(new Tooltip("Change to histogram view."));
        histogramButton.setBackground(invisible);
        histogramButton.borderProperty().bind(Bindings.when(histogramButton.hoverProperty()).then(whenMouseIsOver).otherwise(otherwise));
        histogramButton.setGraphic(new ImageView(new Image("sample/gui/icons/histogram.png")));
        histogramButton.setOnAction(actionEvent -> {
            histogramButton.setBackground(selected);
            quadrilateralButton.setBackground(invisible);

            dataSelectedView = "histogram";
            showDataOnEarth();
        });

        quadrilateralButton.setTooltip(new Tooltip("Change to quadrilateral view."));
        quadrilateralButton.setBackground(invisible);
        quadrilateralButton.borderProperty().bind(Bindings.when(quadrilateralButton.hoverProperty()).then(whenMouseIsOver).otherwise(otherwise));
        quadrilateralButton.setGraphic(new ImageView(new Image("sample/gui/icons/quadrilateral.png")));
        quadrilateralButton.setOnAction(actionEvent -> {
            quadrilateralButton.setBackground(selected);
            histogramButton.setBackground(invisible);

            dataSelectedView = "quadrilateral";
            showDataOnEarth();
        });

        playPauseButton.setTooltip(new Tooltip("Play/Pause time animation."));
        playPauseButton.setBackground(invisible);
        playPauseButton.borderProperty().bind(Bindings.when(playPauseButton.hoverProperty()).then(whenMouseIsOver).otherwise(otherwise));
        playPauseButton.setGraphic(new ImageView(new Image("sample/gui/icons/play.png")));
        playPauseButton.setOnAction(actionEvent -> {
            if (animationActive) {
                animationActive = false;
                playPauseButton.setGraphic(new ImageView(new Image("sample/gui/icons/play.png")));

                yearAnimation.pause();
            } else {
                animationActive = true;
                playPauseButton.setGraphic(new ImageView(new Image("sample/gui/icons/pause.png")));

                yearAnimation.play();
            }
        });

        speedButton.setTooltip(new Tooltip("Fast forward animation."));
        speedButton.setBackground(invisible);
        speedButton.borderProperty().bind(Bindings.when(speedButton.hoverProperty()).then(whenMouseIsOver).otherwise(otherwise));
        speedButton.setGraphic(new ImageView(new Image("sample/gui/icons/fast-forward.png")));
        speedButton.setOnAction(actionEvent -> {
            if (speedRate < 32) speedRate *= 2;
            else speedRate = 1;
            yearAnimation.setRate(speedRate);

            if (!animationActive) playPauseButton.fire();

            speedLabel.setText("x" + speedRate);
        });

        readUpBackWard.setTooltip(new Tooltip("Play animation forward or backward."));
        readUpBackWard.setBackground(invisible);
        readUpBackWard.borderProperty().bind(Bindings.when(readUpBackWard.hoverProperty()).then(whenMouseIsOver).otherwise(otherwise));
        readUpBackWard.setGraphic(new ImageView(new Image("sample/gui/icons/backward.png")));
        readUpBackWard.setOnAction(actionEvent -> {
            if (animationIsForward) {
                animationIsForward = false;
                readUpBackWard.setGraphic(new ImageView(new Image("sample/gui/icons/forward.png")));
            } else {
                animationIsForward = true;
                readUpBackWard.setGraphic(new ImageView(new Image("sample/gui/icons/backward.png")));
            }

            if (!animationActive) playPauseButton.fire();
        });
    }

    /**
     * Functionality: Permettre à l’utilisateur de choisir l’année affichée.
     * Initialize slider and its listeners.
     */
    private void setupSlider() {
        yearSlider.setMin(1880);
        yearSlider.setMax(2020);
        yearSlider.setMinorTickCount(5);
        yearSlider.setMajorTickUnit(35);
        yearSlider.setShowTickLabels(true);

        yearSlider.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double n) {
                if (n == 1880) return "1880";
                if (n == 1915) return "1915";
                if (n == 1950) return "1950";
                if (n == 1985) return "1985";
                return "2020";
            }

            // Not usefull here
            @Override
            public Double fromString(String s) {
                return 0d;
            }
        });

        yearSlider.valueProperty().addListener((observableValue, number, t1) -> {
            currentYear = (int) yearSlider.getValue();
            yearView.setYear(currentYear);
            currentYearTempAnomaly = model.getYearTempAnomaly(currentYear);
            showDataOnEarth();
        });
    }

    /**
     * Functionality:
     *      - Permettre à l’utilisateur de sélectionner une zone du globe directement sur le globe et afficher sa latitude et sa longitude.
     *      - Afficher un graphique 2D de l’évolution des anomalies de température de la zone sélectionnée en fonction des années.
     * 2nd initializer but this time it has access to the model.
     * @param app Application model.
     */
    public void linkModelAndController(App app) {
        model = app;
        earth = new Earth(pane3D, model.getKnownLocations(), model.getGlobalMinAndMax()); // Functionality: Afficher un globe en 3D et permettre à l’utilisateur tourner autour grâce à la souris.

        earth.getRoot3D().setCursor(Cursor.HAND);

        earth.getRoot3D().setOnMouseMoved(mouseEvent -> { // Permettre à l’utilisateur de sélectionner une zone du globe directement sur le globe et afficher sa latitude et sa longitude.
            PickResult pr = mouseEvent.getPickResult(); // To intersection point
            latLonView.updateLatLon(earth.latLonFrom3dCoord(pr.getIntersectedPoint()));
        });

        earth.getRoot3D().setOnMousePressed(mouseEvent -> {
            mousePressedX = mouseEvent.getX();
            mousePressedY = mouseEvent.getY();
            earth.getRoot3D().setCursor(Cursor.CLOSED_HAND);
        });

        earth.getRoot3D().setOnMouseReleased(mouseEvent -> {
            if (mouseEvent.getX() == mousePressedX && mouseEvent.getY() == mousePressedY) {
                PickResult pr = mouseEvent.getPickResult();
                LatLonPair clickedPlace = earth.latLonFrom3dCoord(pr.getIntersectedPoint());

                // We need to be a multiple of 4 for latitude and same for longitude but with an offset of 2 (in our data they are like 2, 6, 10...)
                int correctedLat = clickedPlace.getLat(), correctedLon = clickedPlace.getLon();
                if (clickedPlace.getLat() % 4 != 0)
                    correctedLat = 4 * (Math.round((float) correctedLat / 4));
                if ((clickedPlace.getLon() + 2) % 4 != 0) {
                    correctedLon = 4 * (Math.round((float) correctedLon / 4));
                    if (correctedLon > 178) correctedLon -= 2;
                    else correctedLon += 2;
                }

                if (!popUpActive) { // We create a new pop up if it's not active
                    popUpActive = true;
                    popUpPlot = new PopUpPlot(clickedPlace.toString(), app.getAllTempAnomalyAtLatLon(correctedLat, correctedLon), app.getAvailableYears());
                    popUpPlot.getStage().setOnCloseRequest(windowEvent -> popUpActive = false);
                } else { // We add a curve on current one
                    popUpPlot.addData(clickedPlace.toString(), app.getAllTempAnomalyAtLatLon(correctedLat, correctedLon), app.getAvailableYears());
                }
            }
            earth.getRoot3D().setCursor(Cursor.HAND);
        });

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

    /**
     * This is used when animation is running. Depending on animationIsForward, increment or decrement years.
     */
    private void goToNextYear() {
        if (animationIsForward) {
            if (currentYear <= 2020) currentYear++;
            else currentYear = 1880;
        } else {
            if (currentYear >= 1880) currentYear--;
            else currentYear = 2020;
        }
    }
}
