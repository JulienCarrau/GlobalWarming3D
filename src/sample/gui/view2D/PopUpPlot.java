package sample.gui.view2D;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

public class PopUpPlot {
    private final Stage stage;
    private ArrayList<Integer> years;
    private LineChart<String, Number> lineChart;
    private ArrayList<Series<String, Number>> deleted;

    /**
     * Functionality: Afficher un graphique 2D de l’évolution des anomalies de température de la zone sélectionnée en fonction des années.
     * Create a popup window where data are plot.
     * @param dataName Data name, i.e latitude, longitude.
     * @param data Data to plot (it's all data for one geolocation over all years).
     * @param years All available years.
     */
    public PopUpPlot(String dataName, ArrayList<Float> data, ArrayList<Integer> years) {
        this.years = years;
        deleted = new ArrayList<>();

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Year");
        xAxis.setAnimated(false);
        yAxis.setLabel("Temperature (°C)");

        lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("Temperature evolution threw years at specific locations (click on a line to delete it and CTRL+Z to restore it).");
        lineChart.setPrefHeight(700);
        lineChart.setPrefWidth(1400);

        addData(dataName, data, years);

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
}
