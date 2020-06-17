package sample.gui.view2D;

import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class AutomaticRotationInfo {
    public AutomaticRotationInfo(Pane pane) {
        Group root = new Group();
        Text info = new Text("Press R to activate/desactivate earth's automatic rotation.");
        info.setTranslateY(10);
        root.getChildren().add(info);

        SubScene scene = new SubScene(root, 400, 10);
        scene.setLayoutX(20);
        scene.setLayoutY(640);
        pane.getChildren().add(scene);
    }
}
