package sample.gui.view2D;

import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
        lat = new Text("Lat:  ");
        lat.setFont(new Font(15));
        lat.setTranslateY(15);
        lat.setStrokeWidth(0.5);
        lat.setStroke(Color.WHITE);
        lat.setFill(Color.WHITE);
        root.getChildren().add(lat);

        lon = new Text("Lon: ");
        lon.setFont(new Font(15));
        lon.setTranslateY(35);
        lon.setStrokeWidth(0.5);
        lon.setStroke(Color.WHITE);
        lon.setFill(Color.WHITE);
        root.getChildren().add(lon);

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
        lat.setText("Lat:  " + llp.getLat());
        lon.setText("Lon: " + llp.getLon());
    }

    /**
     * When mouse is not over earth, there is no lat or lon to show.
     */
    public void notOverEarthLatLon() {
        lat.setText("Lat: ");
        lon.setText("Lon:");
    }
}
