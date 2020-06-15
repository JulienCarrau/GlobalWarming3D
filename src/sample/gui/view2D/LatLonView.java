package sample.gui.view2D;

import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import sample.app.LatLonPair;

public class LatLonView {
    private Group root;
    private Text lat, lon;

    /**
     * Functionality: Permettre à l’utilisateur de sélectionner une zone du globe directement sur le globe et afficher sa latitude et sa longitude.
     * LatLonView's constructor, this class allows to show in real time latitude and longitude user mouse's position values in the globe.
     * @param pane Pane where we want to show.
     */
    public LatLonView(Pane pane) {
        root = new Group();
        this.lat = new Text("Lat: ");
        this.lat.setFont(new Font(15));
        this.lat.setTranslateY(15);
        root.getChildren().add(this.lat);

        this.lon = new Text("Lon:");
        this.lon.setFont(new Font(15));
        this.lon.setTranslateY(35);
        root.getChildren().add(this.lon);

        SubScene scene = new SubScene(root, 70, 60);
        scene.setLayoutX(930);
        scene.setLayoutY(620);
        pane.getChildren().add(scene);

    }

    /**
     * Update text values according to given LatLonPair.
     * @param llp LatLonPair for updating texts.
     */
    public void updateLatLon(LatLonPair llp) {
        this.lat.setText("Lat: " + llp.getLat());
        this.lon.setText("Lon:" + llp.getLon());
    }
}
