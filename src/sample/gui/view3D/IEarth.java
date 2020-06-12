package sample.gui.view3D;

import javafx.scene.paint.Color;
import sample.app.LatLonPair;
import sample.app.YearTempAnomaly;

import java.util.ArrayList;

public interface IEarth {
    void addQuadrilateralFilterOverWorld(ArrayList<LatLonPair> locations, YearTempAnomaly anomaly);
    void setColorStep(float minTemp, float maxTemp);
    ArrayList<Color> getColors();
}
