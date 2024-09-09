package cn.octopusyan.alistgui.manager;

import javafx.scene.layout.Pane;
import javafx.stage.Window;

import java.util.HashMap;
import java.util.Map;

/**
 * @author octopus_yan
 */
public class WindowsUtil {
    public static final Map<Pane, Double> paneXOffset = new HashMap<>();
    public static final Map<Pane, Double> paneYOffset = new HashMap<>();

    public static void bindDragged(Pane pane) {
        Window stage = getStage(pane);
        pane.setOnMousePressed(event -> {
            paneXOffset.put(pane, stage.getX() - event.getScreenX());
            paneYOffset.put(pane, stage.getY() - event.getScreenY());
        });
        pane.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + paneXOffset.get(pane));
            stage.setY(event.getScreenY() + paneYOffset.get(pane));
        });
    }

    public static Window getStage(Pane pane) {
        return pane.getScene().getWindow();
    }
}
