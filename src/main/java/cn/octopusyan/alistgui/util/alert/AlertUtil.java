package cn.octopusyan.alistgui.util.alert;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        AlertBuilder alertBuilder = new AlertBuilder(new Alert(Alert.AlertType.INFORMATION), mOwner);
        alertBuilder.content(content).header(null);
        return alertBuilder;
    }

    public static AlertBuilder info() {
        return new AlertBuilder(new Alert(Alert.AlertType.INFORMATION), mOwner);
    }

    public static AlertBuilder error(String message) {
        AlertBuilder alertBuilder = new AlertBuilder(new Alert(Alert.AlertType.ERROR), mOwner);
        alertBuilder.header(null).content(message);
        return alertBuilder;
    }

    public static AlertBuilder warning() {
        return new AlertBuilder(new Alert(Alert.AlertType.WARNING), mOwner);
    }

    public static AlertBuilder exception(Exception ex) {
        return new AlertBuilder(exceptionAlert(ex), mOwner);
    }

    private static Alert exceptionAlert(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText(ex.getClass().getSimpleName());
        alert.setContentText(ex.getMessage());

        // 创建可扩展的异常。
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was :");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // 将可扩展异常设置到对话框窗格中。
        alert.getDialogPane().setExpandableContent(expContent);
        return alert;
    }

    /**
     * 确认对话框
     */
    public static AlertBuilder confirm() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认对话框");
        return new AlertBuilder(alert, mOwner);
    }

    /**
     * 自定义确认对话框 <p>
     * <code>"Cancel"</code> OR <code>"取消"</code> 为取消按钮
     */
    public static AlertBuilder confirm(String... buttons) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        List<ButtonType> buttonList = Arrays.stream(buttons).map((type) -> {
            ButtonBar.ButtonData buttonData = ButtonBar.ButtonData.OTHER;
            if ("cancel".equals(StringUtils.lowerCase(type)) || "取消".equals(type))
                buttonData = ButtonBar.ButtonData.CANCEL_CLOSE;
            return new ButtonType(type, buttonData);
        }).collect(Collectors.toList());

        alert.getButtonTypes().setAll(buttonList);

        return new AlertBuilder(alert, mOwner);
    }

    public static TextInputBuilder input(String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setContentText(content);
        return new TextInputBuilder(dialog, mOwner);
    }

    public static TextInputBuilder input(String content, String defaultResult) {
        TextInputDialog dialog = new TextInputDialog(defaultResult);
        dialog.setContentText(content);
        return new TextInputBuilder(dialog, mOwner);
    }

    @SafeVarargs
    public static <T> ChoiceBuilder<T> choices(String hintText, T... choices) {
        ChoiceDialog<T> dialog = new ChoiceDialog<>(choices[0], choices);
        dialog.setContentText(hintText);
        return new ChoiceBuilder<>(dialog, mOwner);
    }


    public interface OnChoseListener {
        void confirm();

        void cancelOrClose(ButtonType buttonType);
    }

    public interface OnClickListener {
        void onClicked(String result);
    }
}
