package sample.gui.objects2D.legend;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public interface ILegend {
    void setRectanglesWithColors(ArrayList<Color> colors);
    void updateLegendMinAndMax(float min, float max);
}
