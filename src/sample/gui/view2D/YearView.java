package sample.gui.view2D;

import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class YearView {
    private Text year;
    private Group root;

    public YearView(Pane pane, int year) {
        root = new Group();
        this.year = new Text();
        this.year.setTranslateY(10);
        root.getChildren().add(this.year);

        SubScene scene = new SubScene(root, 30, 10);
        scene.setLayoutX(950);
        scene.setLayoutY(600);
        pane.getChildren().add(scene);

        setYear(year);
    }

    public void setYear(int year) {
        this.year.setText(String.valueOf(year));
    }
}
