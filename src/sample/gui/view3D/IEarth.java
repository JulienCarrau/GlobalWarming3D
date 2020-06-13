package sample.gui.view3D;

import javafx.scene.paint.Color;
import sample.app.YearTempAnomaly;

import java.util.ArrayList;

public interface IEarth {
    void updateQuadrilateralFilterOverWorld(YearTempAnomaly anomaly);
    void updateHistogramFilterOverWorld(YearTempAnomaly anomaly);
    ArrayList<Color> getColors();
}
