package sample.gui.view3D;

import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.transform.Rotate;

public class CameraManager {

    private static final double CAMERA_INITIAL_DISTANCE = -5;
    private static final double CAMERA_INITIAL_X_ANGLE = 0.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 0.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double ROTATION_SPEED = 2.0;

    private final Group cameraXform = new Group();
    private final Group cameraXform2 = new Group();
    private Rotate rx = new Rotate();
    private Rotate ry = new Rotate();
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;

    private final Camera camera;

    public CameraManager(Camera cam, Node mainRoot, Group root) {
        camera = cam;

        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(camera);

        rx.setAxis(Rotate.X_AXIS);
        ry.setAxis(Rotate.Y_AXIS);
        cameraXform.getTransforms().addAll(ry, rx);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        rx.setAngle(CAMERA_INITIAL_X_ANGLE);

        // Add keyboard and mouse handler
        handleKeyboard(mainRoot);
        handleMouse(mainRoot);
    }

    private void handleMouse(Node mainRoot) {
        mainRoot.setOnMousePressed(me -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();

            // Set focus on the mainRoot to be able to detect key press
            mainRoot.requestFocus();
        });

        mainRoot.setOnMouseDragged(me -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);

            ry.setAngle(ry.getAngle() + mouseDeltaX * 0.1 * ROTATION_SPEED);
            rx.setAngle(rx.getAngle() - mouseDeltaY * 0.1 * ROTATION_SPEED);
        });
    }

    private void handleKeyboard(Node mainRoot) {
        mainRoot.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ALT) {
                cameraXform2.setTranslateX(0.0);
                cameraXform2.setTranslateY(0.0);

                camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);

                ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                rx.setAngle(CAMERA_INITIAL_X_ANGLE);
            }
        });
    }
}