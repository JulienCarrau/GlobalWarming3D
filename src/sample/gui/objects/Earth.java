package sample.gui.objects;

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

import java.net.URL;

public class Earth {
    private static final double TEXTURE_LAT_OFFSET = -0.2f;
    private static final double TEXTURE_LON_OFFSET = 2.8f;

    public Earth(Pane pane3D) {
        //Create a Pane et graph scene root for the 3D content
        Group root3D = new Group();

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

        // Draw a line

        // Draw an helix

        // Draw city on the earth
        displayTown(root3D, "Brest", 48.447911, -4.418539);
        displayTown(root3D, "Marseille", 43.435555, 5.213611);
        //root3D.getChildren().add(createLine(geoCoordTo3dCoord(48.447911, -4.418539), geoCoordTo3dCoord(43.435555, 5.213611)));

        //addFilterOverWorld(root3D);

        // Add a camera group
        PerspectiveCamera camera = new PerspectiveCamera(true);
        new CameraManager(camera, pane3D, root3D);

        // Add point light
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(-180);
        light.setTranslateY(-90);
        light.setTranslateZ(-120);
        light.getScope().addAll(root3D);
        root3D.getChildren().add(light);

        // Add ambient light
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        ambientLight.getScope().addAll(root3D);
        root3D.getChildren().add(ambientLight);

        SubScene scene = new SubScene(root3D, 1000, 634, true, SceneAntialiasing.BALANCED);
        scene.setCamera(camera);
        pane3D.getChildren().add(scene);
    }

    public void displayTown(Group parent, String name, double lat, double lon) {
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

        parent.getChildren().add(pins);
    }

    // From Rahel LÃ¼thy : https://netzwerg.ch/blog/2015/03/22/javafx-3d-line/
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
    }

    public static Point3D geoCoordTo3dCoord(double lat, double lon, double rayon) {
        double lat_cor = lat + TEXTURE_LAT_OFFSET;
        double lon_cor = lon + TEXTURE_LON_OFFSET;
        return new Point3D(
                -java.lang.Math.sin(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)) * rayon,
                -java.lang.Math.sin(java.lang.Math.toRadians(lat_cor)) * rayon,
                java.lang.Math.cos(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)) * rayon);
    }

    private void addFilterOverWorld(Group parent) {
        PhongMaterial blue = new PhongMaterial();
        blue.setDiffuseColor(new Color(0, 0, 1, 0.05));
        blue.setSpecularColor(new Color(0, 0, 1, 0.05));

        PhongMaterial red = new PhongMaterial();
        red.setDiffuseColor(new Color(1, 0, 0, 0.05));
        red.setSpecularColor(new Color(1, 0, 0, 0.05));

        boolean redOrBlue = true;
        for (int i = -90; i <= 90; i+=10) {
            for (int j = -180; j <= 180; j+=10) {
                if (redOrBlue) addQuadrilateralFromCenterAndAngle(parent, i, j, 10, red);
                else addQuadrilateralFromCenterAndAngle(parent, i, j, 10, blue);
                redOrBlue = !redOrBlue;
            }
        }
    }

    private void addQuadrilateralFromCenterAndAngle(Group parent, float lat, float lon, float angle, PhongMaterial material) {
        Point3D topRight, bottomRight, topLeft, bottomLeft;

        topRight = geoCoordTo3dCoord(lat - angle/2, lon + angle/2, 1.01);
        bottomRight = geoCoordTo3dCoord(lat - angle/2, lon - angle/2, 1.01);
        topLeft = geoCoordTo3dCoord(lat + angle/2, lon + angle/2, 1.01);
        bottomLeft = geoCoordTo3dCoord(lat + angle/2, lon - angle/2, 1.01);

        addQuadrilateral(parent, topRight, bottomRight, topLeft, bottomLeft, material);
    }

    private void addQuadrilateral(Group parent, Point3D topRight, Point3D bottomRight, Point3D topLeft, Point3D bottomLeft, PhongMaterial material) {
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
        parent.getChildren().add(meshView);
    }
}