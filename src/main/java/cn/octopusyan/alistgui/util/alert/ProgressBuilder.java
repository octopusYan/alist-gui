package cn.octopusyan.alistgui.util.alert;

import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.manager.WindowsUtil;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * 加载弹窗
 *
 * @author octopus_yan
 */
public class ProgressBuilder extends BaseBuilder<ProgressBuilder, Dialog<Void>> {

    public ProgressBuilder(Window mOwner) {
        this(new Dialog<>(), mOwner);
    }

    public ProgressBuilder(Dialog<Void> dialog, Window mOwner) {
        super(dialog, mOwner);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getScene().setFill(Color.TRANSPARENT);
        WindowsUtil.bindDragged(dialogPane);
        WindowsUtil.bindShadow(dialogPane);
        WindowsUtil.getStage(dialogPane).initStyle(StageStyle.TRANSPARENT);
        var content = getContent();

        dialogPane.setContent(content);
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

    private Pane getContent() {
        HBox hBox = new HBox();
        hBox.setPrefWidth(350);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10, 0, 10, 0));

        // 取消按钮
        Button cancel = new Button(Context.getLanguageBinding("label.cancel").get());
        cancel.setCancelButton(true);
        cancel.setOnAction(_ -> dialog.close());

        // 进度条 TODO 宽度绑定
        ProgressBar progressBar = new ProgressBar(-1);
        progressBar.prefWidthProperty().bind(Bindings.createDoubleBinding(
                () -> hBox.widthProperty().get() - cancel.widthProperty().get() - 40,
                hBox.widthProperty(), cancel.widthProperty()
        ));

        hBox.getChildren().add(progressBar);
        hBox.getChildren().add(cancel);
        return hBox;
    }

    public ProgressBuilder onCancel(Runnable run) {
        dialog.setOnCloseRequest(_ -> run.run());
        return this;
    }
}
