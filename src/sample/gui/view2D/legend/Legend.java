package sample.gui.view2D.legend;

import javafx.scene.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class Legend implements ILegend {
    private Group root2D;
    private Text minText, maxText;

    /**
     * Legend's constructor.
     * @param pane2D Pane where we store legend.
     */
    public Legend(Pane pane2D) {
        root2D = new Group();

        SubScene scene = new SubScene(root2D, 60, 220);
        scene.setLayoutX(940);
        scene.setLayoutY(207);
        pane2D.getChildren().add(scene);

        setMin(0);
        setMax(0);
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
    @Override
    public void setRectanglesWithColors(ArrayList<Color> colors) {
        double i = 0;
        for (Color c : colors) {
            Rectangle r = new Rectangle(15, 15, new Color(c.getRed(), c.getGreen(), c.getBlue(), 1));
            r.setTranslateX(15);
            r.setTranslateY(180 - i);
            i += 15;
            root2D.getChildren().add(r);
        }
    }

    /**
     * Delete min and max texts and replace them by new ones with the right values.
     * @param min New minimal temperature value.
     * @param max New maximal temperature value.
     */
    @Override
    public void updateLegendMinAndMax(float min, float max) {
        root2D.getChildren().remove(minText);
        setMin(min);
        root2D.getChildren().remove(maxText);
        setMax(max);
    }
}
