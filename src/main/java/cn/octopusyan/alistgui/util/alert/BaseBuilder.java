package cn.octopusyan.alistgui.util.alert;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Objects;

/**
 * @author octopus_yan
 */
public abstract class BaseBuilder<T extends BaseBuilder<T, ?>, D extends Dialog<?>> {
    D dialog;

    public BaseBuilder(D dialog, Window mOwner) {
        this.dialog = dialog;
        icon("/assets/logo.png");
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

    public T icon(String path) {
        return icon(new Image(Objects.requireNonNull(this.getClass().getResource(path)).toString()));
    }

    public T icon(Image image) {
        ObservableList<Image> icons = getStage().getIcons();
        if (icons.isEmpty()) {
            Platform.runLater(() -> icons.add(image));
        }
        return (T) this;
    }

    public void show() {
        if (dialog.isShowing()) {
            if (!Objects.equals(dialog.getContentText(), dialog.getContentText()))
                dialog.setOnHidden(_ -> show());
        }
        dialog.showAndWait();
    }

    private Stage getStage() {
        return (Stage) dialog.getDialogPane().getScene().getWindow();
    }
}
