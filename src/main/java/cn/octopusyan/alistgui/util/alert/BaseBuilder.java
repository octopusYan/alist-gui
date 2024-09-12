package cn.octopusyan.alistgui.util.alert;

import javafx.scene.control.Dialog;
import javafx.stage.Window;

/**
 * @author octopus_yan
 */
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

    public D getDialog() {
        return dialog;
    }

    public void show() {
        dialog.showAndWait();
    }

    public void close() {
        if (dialog.isShowing())
            dialog.close();
    }
}
