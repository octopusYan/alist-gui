package cn.octopusyan.alistgui.util.alert;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import java.util.Optional;

/**
 * @author octopus_yan
 */
public class AlertBuilder extends BaseBuilder<AlertBuilder, Alert> {

    public AlertBuilder(Alert alert, Window owner) {
        super(alert, owner);
    }

    /**
     * AlertUtil.confirm
     */
    public void show(AlertUtil.OnClickListener listener) {
        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(r -> listener.onClicked(r.getText()));
    }

    /**
     * AlertUtil.confirm
     */
    public void show(AlertUtil.OnChoseListener listener) {
        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(r -> {
            if (r == ButtonType.OK) {
                listener.confirm();
            } else {
                listener.cancelOrClose(r);
            }
        });
    }
}