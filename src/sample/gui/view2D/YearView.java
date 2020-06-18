package sample.gui.view2D;

import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class YearView {
    private Text year;
    private Group root;

    public YearView(Pane pane, int year) {
        root = new Group();
        this.year = new Text();
        this.year.setFont(new Font(20));
        this.year.setStroke(Color.WHITE);
        this.year.setFill(Color.WHITE);
        this.year.setTranslateY(20);
        root.getChildren().add(this.year);

        SubScene scene = new SubScene(root, 50, 25);
        scene.setLayoutX(20);
        scene.setLayoutY(20);
        pane.getChildren().add(scene);

        setYear(year);
    }

    public void setYear(int year) {
        this.year.setText(String.valueOf(year));
    }
}
