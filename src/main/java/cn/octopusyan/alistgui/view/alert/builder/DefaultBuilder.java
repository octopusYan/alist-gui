package cn.octopusyan.alistgui.view.alert.builder;

import cn.octopusyan.alistgui.base.BaseBuilder;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.util.WindowsUtil;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * 默认弹窗
 *
 * @author octopus_yan
 */
public class DefaultBuilder extends BaseBuilder<DefaultBuilder, Dialog<?>> {

    public DefaultBuilder(Window mOwner) {
        super(new Dialog<>(), mOwner);

        header(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getScene().setFill(Color.TRANSPARENT);
        WindowsUtil.bindDragged(dialogPane);
        WindowsUtil.bindShadow(dialogPane);
        WindowsUtil.getStage(dialogPane).initStyle(StageStyle.TRANSPARENT);

        dialogPane.getButtonTypes().add(new ButtonType(
                Context.getLanguageBinding("label.cancel").get(),
                ButtonType.CANCEL.getButtonData()
        ));

        for (Node child : dialogPane.getChildren()) {
            if (child instanceof ButtonBar) {
                dialogPane.getChildren().remove(child);
                break;
            }
        }
    }

    public DefaultBuilder content(Node content) {
        dialog.getDialogPane().setContent(content);
        return this;
    }
}
