package sample.gui.view3D;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import sample.app.LatLonPair;
import sample.app.YearTempAnomaly;

import java.net.URL;
import java.util.ArrayList;

public class Earth implements IEarth {
    private Group root3D;
    private ArrayList<PhongMaterial> colorGradient; // Index 0 is blue and index 11 is red
    private ArrayList<Color> colors;
    private float colorStep, minGlobalTempAnomaly; // Correspond to how much a temperature needs to vary to change color

    private static final double TEXTURE_LAT_OFFSET = -0.2f;
    private static final double TEXTURE_LON_OFFSET = 2.8f;

    /**
     * Earth constructor, 3D representation of earth.
     * @param pane3D Pane where we want to draw earth into.
     */
    public Earth(Pane pane3D) {
        root3D = new Group();

        // Load geometry
        ObjModelImporter objImporter = new ObjModelImporter();
        try {
            URL modelUrl = this.getClass().getResource("earth/earth.obj");
            objImporter.read(modelUrl);
        } catch (ImportException i) {
            System.out.println(i.getMessage());
        }
        MeshView[] meshViews = objImporter.getImport();

        root3D.getChildren().add(new Group(meshViews));

        // Add a camera group
        PerspectiveCamera camera = new PerspectiveCamera(true);
        new CameraManager(camera, pane3D, root3D);

        // Add ambient light
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        ambientLight.getScope().addAll(root3D);
        root3D.getChildren().add(ambientLight);

        SubScene scene = new SubScene(root3D, 1000, 634, true, SceneAntialiasing.BALANCED);
        scene.setCamera(camera);
        pane3D.getChildren().add(scene);

        setColorGradient();
    }

    /**
     * Initialize color panel for gradient phong materials colors.
     */
    private void setColorGradient() {
        colorGradient = new ArrayList<>();
        PhongMaterial material;

        colors = new ArrayList<>(); // All 12 colors
        colors.add(new Color(0, 0, 1, 0.05));
        colors.add(new Color(0.12, 0.12, 1, 0.05));
        colors.add(new Color(0.29, 0.29, 1, 0.05));
        colors.add(new Color(0.47, 0.47, 1, 0.05));
        colors.add(new Color(0.64, 0.64, 1, 0.05));
        colors.add(new Color(0.75, 0.75, 1, 0.05));
        colors.add(new Color(1, 0.94, 0, 0.05));
        colors.add(new Color(1, 0.81, 0, 0.05));
        colors.add(new Color(1, 0.6, 0, 0.05));
        colors.add(new Color(1, 0.38, 0.01, 0.05));
        colors.add(new Color(1, 0.17, 0.02, 0.05));
        colors.add(new Color(0.94, 0.02, 0.02, 0.05));

        for (int i = 0; i < 12; i++) {
            material = new PhongMaterial();
            material.setSpecularColor(colors.get(i));
            material.setDiffuseColor(colors.get(i));
            colorGradient.add(material);
        }
    }

    /**
     * Get a 3D point according to a latitude, longitude and a shpere radium.
     * @param lat Latitude value.
     * @param lon Longitude value.
     * @param radium Sphere's radium.
     * @return Point3D corresponding to a 3D location in space.
     */
    private Point3D geoCoordTo3dCoord(double lat, double lon, double radium) {
        double lat_cor = lat + TEXTURE_LAT_OFFSET;
        double lon_cor = lon + TEXTURE_LON_OFFSET;
        return new Point3D(
                -java.lang.Math.sin(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)) * radium,
                -java.lang.Math.sin(java.lang.Math.toRadians(lat_cor)) * radium,
                java.lang.Math.cos(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)) * radium);
    }

    /**
     * Draw a quadrilateral map over the earth showing temperatures anomaly.
     * @param locations List of known locations.
     * @param anomaly Temperature anomaly of this year.
     */
    @Override
    public void addQuadrilateralFilterOverWorld(ArrayList<LatLonPair> locations, YearTempAnomaly anomaly) {
        for (LatLonPair pair : locations)
            addQuadrilateralFromCenterAndAngle(pair.getLat(), pair.getLon(), 4, colorGradient.get((int) ((anomaly.getLocalTempAnomaly(pair.getLat(), pair.getLon()) - minGlobalTempAnomaly) / colorStep)));
    }

    /**
     * Set color step (amount of °C to change color) and set min and max temperatures labels in legend.
     * @param minTemp Minimal temperature anomaly.
     * @param maxTemp Maximal temperature anomaly.
     */
    @Override
    public void setColorStep(float minTemp, float maxTemp) {
        minGlobalTempAnomaly = minTemp;
        colorStep = (maxTemp - minTemp) / 11;
    }

    @Override
    public ArrayList<Color> getColors() {
        return colors;
    }

    /**
     * Draw a quadrilateral over earth centered at lat and lon, its width equals angle parameter.
     * @param lat Latitude value.
     * @param lon Longitude value.
     * @param angle Angle (quadrilateral's width).
     * @param material Desired material for this quadrilateral.
     */
    private void addQuadrilateralFromCenterAndAngle(float lat, float lon, float angle, PhongMaterial material) {
        Point3D topRight, bottomRight, topLeft, bottomLeft;

        topRight = geoCoordTo3dCoord(lat - angle/2, lon + angle/2, 1.01);
        bottomRight = geoCoordTo3dCoord(lat - angle/2, lon - angle/2, 1.01);
        topLeft = geoCoordTo3dCoord(lat + angle/2, lon + angle/2, 1.01);
        bottomLeft = geoCoordTo3dCoord(lat + angle/2, lon - angle/2, 1.01);

        addQuadrilateral(topRight, bottomRight, topLeft, bottomLeft, material);
    }

    /**
     * Draw a quadrilateral over earth according to its corners and a material.
     * @param topRight Top Right quadrilateral's corner.
     * @param bottomRight Bottom Right quadrilateral's corner.
     * @param topLeft Top Left quadrilateral's corner.
     * @param bottomLeft Bottom Left quadrilateral's corner.
     * @param material Desired material for this quadrilateral.
     */
    private void addQuadrilateral(Point3D topRight, Point3D bottomRight, Point3D topLeft, Point3D bottomLeft, PhongMaterial material) {
        final TriangleMesh triangleMesh = new TriangleMesh();

        final float[] points = {
            (float)topRight.getX(), (float)topRight.getY(), (float)topRight.getZ(),
            (float)topLeft.getX(), (float)topLeft.getY(), (float)topLeft.getZ(),
            (float)bottomLeft.getX(), (float)bottomLeft.getY(), (float)bottomLeft.getZ(),
            (float)bottomRight.getX(), (float)bottomRight.getY(), (float)bottomRight.getZ() };

        final float[] texturesCoords = {
            1, 1,
            1, 0,
            0, 1,
            0, 0
        };

        final int[] faces = {
            0, 1, 1, 0, 2, 2,
            0, 1, 2, 2, 3, 3
        };

        /*

            points :
            1      0
            -------     texture:
            |    /|     1,1(0)  1,0(1)
            |  /  |     ---------
            |/    |     |       |
            -------     |       |
            2      3    ---------
                        0,1(2)  0,0(3)

        */

        triangleMesh.getPoints().setAll(points);
        triangleMesh.getTexCoords().setAll(texturesCoords);
        triangleMesh.getFaces().setAll(faces);

        final MeshView meshView = new MeshView(triangleMesh);
        meshView.setMaterial(material);
        root3D.getChildren().add(meshView);
    }
}

/*
    //earth.displayTown("Brest", 48.447911, -4.418539);
    //earth.displayTown("Marseille", 43.435555, 5.213611);
    public void displayTown(String name, double lat, double lon) {
        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.GREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        Sphere pins = new Sphere(0.01);
        pins.setId(name);
        pins.setMaterial(greenMaterial);

        Point3D position = geoCoordTo3dCoord(lat, lon, 1);

        pins.setTranslateX(position.getX());
        pins.setTranslateY(position.getY());
        pins.setTranslateZ(position.getZ());

        root3D.getChildren().add(pins);
    }*/

    /* From Rahel LÃ¼thy : https://netzwerg.ch/blog/2015/03/22/javafx-3d-line/
    public Cylinder createLine(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(0.01f, height);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }*/