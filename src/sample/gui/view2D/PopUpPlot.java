package sample.gui.view2D;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Objects;

public class PopUpPlot {
    private final Stage stage;
    private XYChart.Data<String, Number> currentYearIndicator;
    private ArrayList<Integer> years;
    private LineChartWithMarkers<String, Number> lineChart;
    private ArrayList<Series<String, Number>> deleted;

    /**
     * Functionality: Afficher un graphique 2D de l’évolution des anomalies de température de la zone sélectionnée en fonction des années.
     * Create a popup window where data are plot.
     * @param dataName Data name, i.e latitude, longitude.
     * @param data Data to plot (it's all data for one geolocation over all years).
     * @param years All available years.
     */
    public PopUpPlot(String dataName, ArrayList<Float> data, ArrayList<Integer> years, int currentYear) {
        this.years = years;
        deleted = new ArrayList<>();

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Year");
        xAxis.setAnimated(false);
        yAxis.setLabel("Temperature (°C)");

        lineChart = new LineChartWithMarkers<>(xAxis, yAxis);

        lineChart.setTitle("Temperature evolution threw years at specific locations (click on a line to delete it and CTRL+Z to restore it).");
        lineChart.setPrefHeight(700);
        lineChart.setPrefWidth(1400);

        addData(dataName, data, years);

        currentYearIndicator = new XYChart.Data<>(String.valueOf(currentYear), 0); // Init current year
        lineChart.addVerticalValueMarker(currentYearIndicator);

        stage = new Stage();

        stage.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getText().toLowerCase().equals("z") && keyEvent.isShortcutDown()) controlZ();
        });

        stage.initModality(Modality.NONE);
        Scene scene = new Scene(lineChart, lineChart.getPrefWidth(), lineChart.getPrefHeight());
        stage.setTitle("Temperature evolution threw years at specific locations");
        stage.setScene(scene);
        stage.show();

        stage.addEventHandler(MouseEvent.ANY, mouseEvent -> scene.setCursor(Cursor.CROSSHAIR)); // Change cursor for this window
    }

    /**
     * Stage getter. It's used to setup a listener when stage is closed by user.
     * @return Stage.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Plot new data on line chart.
     * @param dataName Data name, i.e latitude, longitude.
     * @param data Data to plot (it's all data for one geolocation over all years).
     * @param years All available years.
     */
    public void addData(String dataName, ArrayList<Float> data, ArrayList<Integer> years) {
        Series<String, Number> series = new Series<>();
        series.setName(dataName);
        int i = 0;
        for (Float temperature : data) {
            if (temperature.equals(Float.NaN)) series.getData().add(new XYChart.Data<>(String.valueOf(years.get(i)), 0));
            else series.getData().add(new XYChart.Data<>(String.valueOf(years.get(i)), temperature));
            i++;
        }
        lineChart.getData().add(series);
        series.getNode().setOnMouseClicked(mouseEvent -> {
            deleted.add(series);
            lineChart.getData().remove(series);
        });
    }

    /**
     * Update current year indicator (red line).
     * @param year New current year.
     */
    public void updateCurrentYear(int year) {
        lineChart.removeVerticalValueMarker(currentYearIndicator);
        currentYearIndicator = new XYChart.Data<>(String.valueOf(year), 0);
        lineChart.addVerticalValueMarker(currentYearIndicator);
    }

    /**
     * Restore last curve when CTRL+Z is pressed.
     */
    private void controlZ() {
        if (deleted.size() > 0) {
            Series<String, Number> s = deleted.get(deleted.size() - 1);
            lineChart.getData().add(s);
            s.getNode().setOnMouseClicked(mouseEvent -> {
                deleted.add(s);
                lineChart.getData().remove(s);
            });
            deleted.remove(s);
        }
    }

    /**
     * Thanks to https://stackoverflow.com/questions/28952133/how-to-add-two-vertical-lines-with-javafx-linechart.
     * This class is an "extension" of LineChart, it allows to draw and remove vertical markers.
     * @param <String> xAxis containing strings.
     * @param <Number> yAxis containing numbers.
     */
    private static class LineChartWithMarkers<String, Number> extends LineChart<String, Number> {
        private ObservableList<Data<String, Number>> verticalMarkers;

        public LineChartWithMarkers(Axis<String> xAxis, Axis<Number> yAxis) {
            super(xAxis, yAxis);
            verticalMarkers = FXCollections.observableArrayList(data -> new Observable[] {data.XValueProperty()});
            verticalMarkers.addListener((InvalidationListener)observable -> layoutPlotChildren());
        }

        public void addVerticalValueMarker(Data<String, Number> marker) {
            Objects.requireNonNull(marker, "the marker must not be null");
            if (verticalMarkers.contains(marker)) return;
            Line line = new Line();
            line.setStroke(Color.RED);
            marker.setNode(line);
            getPlotChildren().add(line);
            verticalMarkers.add(marker);
        }

        public void removeVerticalValueMarker(Data<String, Number> marker) {
            Objects.requireNonNull(marker, "the marker must not be null");
            if (marker.getNode() != null) {
                getPlotChildren().remove(marker.getNode());
                marker.setNode(null);
            }
            verticalMarkers.remove(marker);
        }

        @Override
        protected void layoutPlotChildren() {
            super.layoutPlotChildren();
            for (Data<String, Number> verticalMarker : verticalMarkers) {
                Line line = (Line) verticalMarker.getNode();
                line.setStartX(getXAxis().getDisplayPosition(verticalMarker.getXValue()) + 0.5);  // 0.5 for crispness
                line.setEndX(line.getStartX());
                line.setStartY(0d);
                line.setEndY(getBoundsInLocal().getHeight());
                line.toFront();
            }
        }
    }
}
