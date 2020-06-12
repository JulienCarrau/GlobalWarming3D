package sample.gui.view2D.legend;

import javafx.scene.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class Legend {
    private Group root2D;
    private Text minText, maxText;

    /**
     * Legend's constructor.
     * @param pane2D Pane where we store legend.
     * @param min Minimal temperature anomaly.
     * @param max Maximal temperature anomaly.
     * @param colors ArrayList of all colors present in legend.
     */
    public Legend(Pane pane2D, float min, float max, ArrayList<Color> colors) {
        root2D = new Group();

        SubScene scene = new SubScene(root2D, 60, 220);
        scene.setLayoutX(940);
        scene.setLayoutY(207);
        pane2D.getChildren().add(scene);

        setMin(min);
        setMax(max);

        setRectanglesWithColors(colors);
    }

    /**
     * Set minimal temperature text to value.
     * @param value Minimal temperature value.
     */
    private void setMin(float value) {
        minText = new Text(String.format("%.2f", value) + "°C");
        minText.setTranslateX(10);
        minText.setTranslateY(210);
        root2D.getChildren().add(minText);
    }

    /**
     * Set maximal temperature text to value.
     * @param value Maximal temperature value.
     */
    private void setMax(float value) {
        maxText = new Text(String.format("%.2f", value) + "°C");
        maxText.setTranslateX(10);
        maxText.setTranslateY(10);
        root2D.getChildren().add(maxText);
    }

    /**
     * Set legend gradient colors view.
     * @param colors List of displayed colors.
     */
    private void setRectanglesWithColors(ArrayList<Color> colors) {
        double i = 0;
        for (Color c : colors) {
            Rectangle r = new Rectangle(15, 15, new Color(c.getRed(), c.getGreen(), c.getBlue(), 1));
            r.setTranslateX(15);
            r.setTranslateY(180 - i);
            i += 15;
            root2D.getChildren().add(r);
        }
    }
}
