package sample.gui.view2D;

import javafx.scene.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class Legend {
    private Group root;
    private Text minText, maxText, middleText;

    /**
     * Legend's constructor.
     * @param pane Pane where we store legend.
     * @param min Minimal temperature anomaly.
     * @param max Maximal temperature anomaly.
     * @param colors ArrayList of all colors present in legend.
     */
    public Legend(Pane pane, float min, float max, ArrayList<Color> colors) {
        root = new Group();

        SubScene scene = new SubScene(root, 60, 220);
        scene.setLayoutX(940);
        scene.setLayoutY(235);
        pane.getChildren().add(scene);

        minText = new Text();
        minText.setTranslateX(10);
        minText.setTranslateY(210);
        root.getChildren().add(minText);

        maxText = new Text();
        maxText.setTranslateX(10);
        maxText.setTranslateY(10);
        root.getChildren().add(maxText);

        middleText = new Text();
        middleText.setTranslateX(32);
        middleText.setTranslateY(110);
        root.getChildren().add(middleText);

        setMin(min);
        setMax(max);
        middleText.setText("0°C");

        setRectanglesWithColors(colors);
    }

    /**
     * Set minimal temperature text to value.
     * @param value Minimal temperature value.
     */
    private void setMin(float value) {
        minText.setText(String.format("%.2f", value) + "°C");
    }

    /**
     * Set maximal temperature text to value.
     * @param value Maximal temperature value.
     */
    private void setMax(float value) {
        maxText.setText(String.format("%.2f", value) + "°C");
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
            root.getChildren().add(r);
        }
    }
}
