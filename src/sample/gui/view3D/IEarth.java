package sample.gui.view3D;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import sample.app.LatLonPair;
import sample.app.YearTempAnomaly;

import java.util.ArrayList;

public interface IEarth {
    void updateQuadrilateralFilterOverWorld(YearTempAnomaly anomaly);
    void updateHistogramFilterOverWorld(YearTempAnomaly anomaly);
    Group getRoot3D();
    ArrayList<Color> getColors();
    LatLonPair latLonFrom3dCoord(Point3D position);
}
