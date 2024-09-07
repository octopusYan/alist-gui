package cn.octopusyan.alistgui.util.alert;

import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Objects;

/**
 * @author octopus_yan
 */
public abstract class BaseBuilder<T extends Dialog<R>, R> {
    T alert;

    public BaseBuilder(T alert, Window mOwner) {
        this.alert = alert;
        if (mOwner != null)
            this.alert.initOwner(mOwner);
    }

    public BaseBuilder<T, R> title(String title) {
        alert.setTitle(title);
        return this;
    }

    public BaseBuilder<T, R> header(String header) {
        alert.setHeaderText(header);
        return this;
    }

    public BaseBuilder<T, R> content(String content) {
        alert.setContentText(content);
        return this;
    }

    public BaseBuilder<T, R> icon(String path) {
        return icon(new Image(Objects.requireNonNull(this.getClass().getResource(path)).toString()));
    }

    public BaseBuilder<T, R> icon(Image image) {
        getStage().getIcons().add(image);
        return this;
    }

    public void show() {
        if (alert.isShowing()) {
            if (!Objects.equals(alert.getContentText(), alert.getContentText()))
                alert.setOnHidden(_ -> show());
        }
        alert.showAndWait();
    }

    private Stage getStage() {
        return (Stage) alert.getDialogPane().getScene().getWindow();
    }
}
