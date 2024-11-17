package cn.octopusyan.upgrade.alert;

import cn.octopusyan.upgrade.alert.builder.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * 弹窗工具
 *
 * @author octopus_yan@foxmail.com
 */
public class AlertUtil {
    private static Window mOwner;

    public static void initOwner(Stage stage) {
        AlertUtil.mOwner = stage;
    }

    public static AlertBuilder info(String content) {
        return info().content(content).header(null);
    }

    public static AlertBuilder info() {
        return alert(Alert.AlertType.INFORMATION);
    }

    public static AlertBuilder error(String message) {
        return alert(Alert.AlertType.ERROR).header(null).content(message);
    }

    public static AlertBuilder warning() {
        return alert(Alert.AlertType.WARNING);
    }

    public static AlertBuilder exception(Exception ex) {
        return alert(Alert.AlertType.ERROR).exception(ex);
    }

    /**
     * 确认对话框
     */
    public static AlertBuilder confirm() {
        return alert(Alert.AlertType.CONFIRMATION);
    }

    /**
     * 自定义确认对话框 <p>
     *
     * @param buttons <code>"Cancel"</code> OR <code>"取消"</code> 为取消按钮
     */
    public static AlertBuilder confirm(String... buttons) {
        return confirm().buttons(buttons);
    }

    public static AlertBuilder confirm(ButtonType... buttons) {
        return confirm().buttons(buttons);
    }

    public static AlertBuilder alert(Alert.AlertType type) {
        return new AlertBuilder(mOwner, type);
    }

    public interface OnChoseListener {
        void confirm();

        default void cancelOrClose(ButtonType buttonType) {
        }
    }

    public interface OnClickListener {
        void onClicked(String result);
    }
}
