package cn.octopusyan.alistgui.base;

import cn.octopusyan.alistgui.manager.ConfigManager;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.stage.Window;
import lombok.Getter;

/**
 * @author octopus_yan
 */
@Getter
public abstract class BaseBuilder<T extends BaseBuilder<T, ?>, D extends Dialog<?>> {
    protected D dialog;

    public BaseBuilder(D dialog, Window mOwner) {
        this.dialog = dialog;
        if (mOwner != null)
            this.dialog.initOwner(mOwner);
    }

    public T title(String title) {
        dialog.setTitle(title);
        return (T) this;
    }

    public T header(String header) {
        dialog.setHeaderText(header);
        return (T) this;
    }

    public T content(String content) {
        dialog.setContentText(content);
        return (T) this;
    }

    public void show() {

        Node dialogPane = dialog.getDialogPane().getContent();
        if (dialogPane != null && ConfigManager.theme().isDarkMode()) {
            dialogPane.setStyle(STR."""
                    \{dialogPane.getStyle()}
                    -fx-border-color: rgb(209, 209, 214, 0.5);
                    -fx-border-width: 1;
                    -fx-border-radius: 10;
                    """);
        }

        Platform.runLater(() -> dialog.showAndWait());
    }

    public void close() {
        if (dialog.isShowing())
            dialog.close();
    }
}
