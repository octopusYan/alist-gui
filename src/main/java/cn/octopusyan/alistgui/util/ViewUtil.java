package cn.octopusyan.alistgui.util;

import cn.octopusyan.alistgui.Application;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * 工具
 *
 * @author octopus_yan
 */
public class ViewUtil {
    // 获取系统缩放比
    public static final double scaleX = Screen.getPrimary().getOutputScaleX();
    public static final double scaleY = Screen.getPrimary().getOutputScaleY();


    private static final Map<Pane, Double> paneXOffset = new HashMap<>();
    private static final Map<Pane, Double> paneYOffset = new HashMap<>();

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

    public static void unbindDragged(Pane pane) {
        pane.setOnMousePressed(null);
        pane.setOnMouseDragged(null);
        paneXOffset.remove(pane);
        paneYOffset.remove(pane);
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

    public static Stage getStage() {
        return Application.getPrimaryStage();
    }

    public static Stage getStage(Pane pane) {
        try {
            return (Stage) pane.getScene().getWindow();
        } catch (Throwable e) {
            return getStage();
        }
    }
}
