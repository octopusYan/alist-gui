package cn.octopusyan.alistgui.util;

import cn.octopusyan.alistgui.Application;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author octopus_yan
 */
public class WindowsUtil {
    public static final Map<Pane, Double> paneXOffset = new HashMap<>();
    public static final Map<Pane, Double> paneYOffset = new HashMap<>();

    public static void bindShadow(Pane pane) {
        pane.setStyle("""
                -fx-background-radius: 5;
                -fx-border-radius: 5;
                -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 15, 0, 0, 0);
                -fx-background-insets: 20;
                -fx-padding: 20;
                """);
    }

    public static void bindDragged(Pane pane) {
        Stage stage = getStage(pane);
        bindDragged(pane, stage);
    }

    public static void bindDragged(Pane pane, Stage stage) {
        pane.setOnMousePressed(event -> {
            paneXOffset.put(pane, stage.getX() - event.getScreenX());
            paneYOffset.put(pane, stage.getY() - event.getScreenY());
        });
        pane.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + paneXOffset.get(pane));
            stage.setY(event.getScreenY() + paneYOffset.get(pane));
        });
    }

    public static Stage getStage(Pane pane) {
        try {
            return (Stage) pane.getScene().getWindow();
        } catch (Throwable e) {
            return Application.getPrimaryStage();
        }
    }
}
