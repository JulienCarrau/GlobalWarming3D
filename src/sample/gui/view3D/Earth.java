package sample.gui.view3D;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import sample.app.LatLonPair;
import sample.app.YearTempAnomaly;

import java.net.URL;
import java.util.ArrayList;

public class Earth implements IEarth {
    private Group root3D, histogramGroup, quadrilateralGroup; // Groups of 3D shapes
    private CameraManager cameraManager;
    private boolean histogramViewEnabled;
    PhongMaterial transparent = new PhongMaterial(); // Transparent material
    private ArrayList<PhongMaterial> colorGradient, cylinderColorGradient; // Index 0 is blue and index 11 is red, cylinderColorGradient are opaque
    private final ArrayList<LatLonPair> knownLocations;
    private ArrayList<Color> colors;
    private final float colorStep, minGlobalTempAnomaly, maxGlobalTempAnomaly; // Correspond to how much a temperature needs to vary to change color

    private static final double TEXTURE_LAT_OFFSET = -0.2f;
    private static final double TEXTURE_LON_OFFSET = 2.8f;

    /**
     * Earth constructor, 3D representation of earth.
     * @param pane3D Pane where we want to draw earth into.
     * @param knownLocations Known earth locations.
     * @param globalMinAndMaxTemp Global minimal and maximal temperatures anomaly.
     */
    public Earth(Pane pane3D, ArrayList<LatLonPair> knownLocations, ArrayList<Float> globalMinAndMaxTemp) {
        this.knownLocations = knownLocations;
        histogramViewEnabled = false;

        minGlobalTempAnomaly = globalMinAndMaxTemp.get(0);
        maxGlobalTempAnomaly = globalMinAndMaxTemp.get(1);
        colorStep = (maxGlobalTempAnomaly - minGlobalTempAnomaly) / 11;

        root3D = new Group();

        transparent.setDiffuseColor(Color.TRANSPARENT);

        initializeQuadrilateralFilterOverWorld();
        initializeHistogramFilterOverWorld();

        // Load geometry
        ObjModelImporter objImporter = new ObjModelImporter();
        try {
            URL modelUrl = this.getClass().getResource("earth/earth.obj");
            objImporter.read(modelUrl);
        } catch (ImportException i) {
            System.out.println(i.getMessage());
        }
        MeshView[] meshViews = objImporter.getImport();
        Group earth = new Group(meshViews);
        root3D.getChildren().add(earth);

        // Add a camera group
        PerspectiveCamera camera = new PerspectiveCamera(true);
        cameraManager = new CameraManager(camera, pane3D, root3D);

        // Add ambient light
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        ambientLight.getScope().addAll(root3D);
        root3D.getChildren().add(ambientLight);

        SubScene scene = new SubScene(root3D, 1000, 670, true, SceneAntialiasing.BALANCED);
        scene.setCamera(camera);
        pane3D.getChildren().add(scene);

        setColorGradient();
    }

    /**
     * Initialize color panel for gradient phong materials colors.
     */
    private void setColorGradient() {
        colorGradient = new ArrayList<>();
        cylinderColorGradient = new ArrayList<>();
        PhongMaterial material;

        colors = new ArrayList<>(); // All 12 colors
        colors.add(new Color(0, 0, 1, 0.05));
        colors.add(new Color(0.12, 0.12, 1, 0.05));
        colors.add(new Color(0.29, 0.29, 1, 0.05));
        colors.add(new Color(0.47, 0.47, 1, 0.05));
        colors.add(new Color(0.64, 0.64, 1, 0.05));
        colors.add(new Color(0.75, 0.75, 1, 0.05));
        colors.add(new Color(0.9, 0.84, 0, 0.05));
        colors.add(new Color(0.9, 0.71, 0, 0.05));
        colors.add(new Color(0.9, 0.5, 0, 0.05));
        colors.add(new Color(0.9, 0.28, 0, 0.05));
        colors.add(new Color(0.9, 0.07, 0, 0.05));
        colors.add(new Color(0.84, 0, 0, 0.05));

        for (Color color : colors) {
            material = new PhongMaterial(); // This one is nearly transparent
            material.setSpecularColor(color);
            material.setDiffuseColor(color);
            colorGradient.add(material);

            material = new PhongMaterial(); // This one is opaque
            material.setSpecularColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 1));
            material.setDiffuseColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 1));
            cylinderColorGradient.add(material);
        }
    }

    /**
     * Get a 3D point according to a latitude, longitude and a shpere radius.
     * @param lat Latitude value.
     * @param lon Longitude value.
     * @param radius Sphere's radius.
     * @return Point3D corresponding to a 3D location in space.
     */
    private Point3D geoCoordTo3dCoord(double lat, double lon, double radius) {
        double lat_cor = lat + TEXTURE_LAT_OFFSET;
        double lon_cor = lon + TEXTURE_LON_OFFSET;
        return new Point3D(
                -java.lang.Math.sin(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)) * radius,
                -java.lang.Math.sin(java.lang.Math.toRadians(lat_cor)) * radius,
                java.lang.Math.cos(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)) * radius);
    }

    /**
     * Reverse geoCoordTo3dCoord, it outputs a LatLonPair from a given 3D position.
     * We assume that radius is 1 here.
     * @param position 3D position to transpose into latitude and longitude.
     * @return LatLonPair.
     */
    public LatLonPair latLonFrom3dCoord(Point3D position) {
        double lon;
        double lat;
        lat = Math.toDegrees(Math.acos(position.getY())) - 90 - TEXTURE_LAT_OFFSET;
        lon = ((180 + (-Math.atan2(position.getX() , position.getZ())) * 180 / Math.PI) % 360) - 180 - TEXTURE_LON_OFFSET;
        if (lon > 180) lon = 180; // Remap to be sure we don't go further, we need to remap because of textures offsets
        else if (lon < -180) lon = -180;

        return new LatLonPair((int) lat, (int) lon);
    }

    /**
     * CameraManager getter. Allow from controller to access directly to cameraManager.
     * @return CameraManager instance.
     */
    @Override
    public CameraManager getCameraManager() {
        return cameraManager;
    }

    /**
     * Initialize quadrilateral group. This group contains all quadrilaterals, they are all transparent at first.
     */
    private void initializeQuadrilateralFilterOverWorld() {
        quadrilateralGroup = new Group();

        for (LatLonPair pair : knownLocations)
            addQuadrilateralFromCenterAndAngle(pair.getLat(), pair.getLon(), 4, transparent);

        root3D.getChildren().add(quadrilateralGroup);
    }

    /**
     * Initialize histogram group. This group contains all histograms, they are all transparent at first.
     */
    private void initializeHistogramFilterOverWorld() {
        histogramGroup = new Group();

        for (LatLonPair pair : knownLocations)
            addHistogramBarFromCenterAndDiameter(pair.getLat(), pair.getLon(), 0, transparent);

        root3D.getChildren().add(histogramGroup);
    }

    /**
     * Fonctionality: Afficher toutes les valeurs des anomalies de température d’une année donnée sur le globe sous forme quadrilatère de couleur (vous pourrez vous aider du tutoriel réalisé à la première séance). Vous devrez faire en sorte que la couleur chance en fonction de la valeur de l’anomalie pour chacune des zones (en utilisant, par exemple, un dégradé de couleur allant du bleu au rouge). Afficher une légende indiquant les températures minimales et maximale ainsi que les couleurs associées.
     * Draw a quadrilateral map over the earth showing temperatures anomaly. Quadrilateral's colors may vary according to anomly's temperature.
     * If temperature anomaly is null (NA), it's not drawn.
     * @param anomaly Temperature anomaly of this year.
     */
    @Override
    public void updateQuadrilateralFilterOverWorld(YearTempAnomaly anomaly) {
        if (histogramViewEnabled) {
            root3D.getChildren().remove(histogramGroup);
            histogramViewEnabled = false;
            initializeHistogramFilterOverWorld(); // make this filter transparent
        }

        Float temperature;
        int i = 0, colorIndex;
        for (LatLonPair pair : knownLocations) {
            temperature = anomaly.getLocalTempAnomaly(pair.getLat(), pair.getLon());
            if (!temperature.equals(Float.NaN)) {
                if (temperature > 0)
                    colorIndex = Math.min(6 + (int) ((anomaly.getLocalTempAnomaly(pair.getLat(), pair.getLon())) / colorStep), 11);
                else
                    colorIndex = (int) ((anomaly.getLocalTempAnomaly(pair.getLat(), pair.getLon()) - minGlobalTempAnomaly) / colorStep);
                ((MeshView) quadrilateralGroup.getChildren().get(i)).setMaterial(colorGradient.get(colorIndex));
            } else
                ((MeshView) quadrilateralGroup.getChildren().get(i)).setMaterial(transparent);
            i++;
        }
    }

    /**
     * Functionality: Afficher toutes les valeurs des anomalies de température positives d’une année donnée sur le globe sous forme d’histogrammes. Ces histogrammes seront centrés dans la zone associée et alignés sur une ligne passant par le centre du globe. La taille des histogrammes sera proportionnelle à la valeur de l’anomalie de température. L’histogramme pourra également avoir une couleur liée à la valeur. Les valeurs négatives pourront être représentées par des petits carrés de couleur au centre de la zone (en utilisant un dégradé de bleu par exemple). Afficher une légende indiquant les températures minimales et maximale ainsi que les couleurs associées.
     * Draw an histogram map over the earth showing temperatures anomaly. Bar's colors and height may vary according to anomly's temperature.
     * If temperature anomaly is negative or null (NA), it's not drawn.
     * @param anomaly Temperature anomaly of this year.
     */
    @Override
    public void updateHistogramFilterOverWorld(YearTempAnomaly anomaly) {
        if (!histogramViewEnabled) {
            root3D.getChildren().remove(quadrilateralGroup);
            histogramViewEnabled = true;
            initializeQuadrilateralFilterOverWorld(); // make this filter transparent
        }

        Float temperature;
        int i = 0, colorIndex;
        for (LatLonPair pair : knownLocations) {
            temperature = anomaly.getLocalTempAnomaly(pair.getLat(), pair.getLon());
            if (!temperature.equals(Float.NaN) && temperature > 0) {
                colorIndex = 6 + (int) (anomaly.getLocalTempAnomaly(pair.getLat(), pair.getLon()) / colorStep); // Index for positive temperature is between 6 and 11
                updateBar((Cylinder) histogramGroup.getChildren().get(i), pair.getLat(), pair.getLon(), temperature, cylinderColorGradient.get(Math.min(colorIndex, 11)));
            } else
                ((Cylinder) histogramGroup.getChildren().get(i)).setMaterial(transparent);
            i++;
        }
    }

    /**
     * Root3D getter.
     * Used to setup a listener to track mouse's latitude and longitude.
     * @return Root3D group.
     */
    @Override
    public Group getRoot3D() {
        return root3D;
    }

    /**
     * Colors getter.
     * @return ArrayList<Color> containing all temperature colors.
     */
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

        MeshView meshView = new MeshView(triangleMesh);
        meshView.setMaterial(material);
        quadrilateralGroup.getChildren().add(meshView);
    }

    /**
     * Draw an histogram bar over earth according to a geological position, anomaly's intensity (height) and a material.
     * @param lat Latitude value.
     * @param lon Longitude value.
     * @param height Temperature Anomaly's intensity.
     * @param material Desired material for this bar.
     */
    private void addHistogramBarFromCenterAndDiameter(float lat, float lon, float height, PhongMaterial material) {
        Point3D base = geoCoordTo3dCoord(lat, lon, 0);
        Point3D top = geoCoordTo3dCoord(lat, lon, 1 + Math.round(100 * height) / (100f * (3 * maxGlobalTempAnomaly))); // 3 is a reduction factor

        addHistogramBar(base, top, material);
    }

    /**
     * Draw an histogram bar according to a start, end point and material.
     * @param base Start point.
     * @param top End (top) point.
     * @param material Desired material for this bar.
     */
    private void addHistogramBar(Point3D base, Point3D top, PhongMaterial material) {
        Cylinder bar = createBar(base, top);
        bar.setMaterial(material);

        histogramGroup.getChildren().add(bar);
    }

    /**
     * From Rahel LÃ¼thy : https://netzwerg.ch/blog/2015/03/22/javafx-3d-line/
     * Draw a cylinder according to a start and an end point. Its radium is 0.01.
     * @param origin Start point.
     * @param target End point.
     * @return Corresponding cylinder linking start and end points.
     */
    private Cylinder createBar(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder bar = new Cylinder(0.01f, height, 8); // Less divisions = more fluidity

        bar.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return bar;
    }

    /**
     * From Rahel LÃ¼thy : https://netzwerg.ch/blog/2015/03/22/javafx-3d-line/
     * Update a cylinder, update material and height.
     * @param bar Cylinder to update.
     * @param lat Latitude value.
     * @param lon Longitude value.
     * @param temperature Temperature, correspond to future height.
     * @param material Desired material for this bar.
     */
    private void updateBar(Cylinder bar, float lat, float lon, float temperature, PhongMaterial material) {
        Point3D origin = geoCoordTo3dCoord(lat, lon, 0);
        Point3D target = geoCoordTo3dCoord(lat, lon, 1 + Math.round(100 * temperature) / (100f * (3 * maxGlobalTempAnomaly))); // 3 is a reduction factor

        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        bar.setMaterial(material);
        bar.setHeight(height);
        bar.getTransforms().clear();
        bar.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);
    }
}