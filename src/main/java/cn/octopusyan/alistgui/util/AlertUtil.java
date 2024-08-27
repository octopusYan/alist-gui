package cn.octopusyan.alistgui.util;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 弹窗工具
 *
 * @author octopus_yan@foxmail.com
 */
public class AlertUtil {
    private static Window mOwner;
    private static Builder builder;

    public static void initOwner(Stage stage) {
        AlertUtil.mOwner = stage;
    }

    public static class Builder<T extends Dialog> {
        T alert;

        public Builder(T alert) {
            this.alert = alert;
            if (mOwner != null) this.alert.initOwner(mOwner);
        }

        public Builder<T> title(String title) {
            alert.setTitle(title);
            return this;
        }

        public Builder<T> header(String header) {
            alert.setHeaderText(header);
            return this;
        }

        public Builder<T> content(String content) {
            alert.setContentText(content);
            return this;
        }

        public Builder<T> icon(String path) {
            icon(new Image(Objects.requireNonNull(this.getClass().getResource(path)).toString()));
            return this;
        }

        public Builder<T> icon(Image image) {
            getStage().getIcons().add(image);
            return this;
        }

        public void show() {
            if (AlertUtil.builder == null) {
                AlertUtil.builder = this;
            } else if (AlertUtil.builder.alert.isShowing()) {
                if (!Objects.equals(AlertUtil.builder.alert.getContentText(), alert.getContentText()))
                    ((Alert) AlertUtil.builder.alert).setOnHidden(event -> {
                        AlertUtil.builder = null;
                        show();
                    });
            }
            alert.showAndWait();
        }

        /**
         * AlertUtil.confirm
         */
        public void show(OnClickListener listener) {

            Optional<ButtonType> result = alert.showAndWait();

            listener.onClicked(result.get().getText());
        }

        /**
         * AlertUtil.confirm
         */
        public void show(OnChoseListener listener) {
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                listener.confirm();
            } else {
                listener.cancelOrClose(result.get());
            }
        }

        /**
         * AlertUtil.input
         * 如果用户点击了取消按钮,将会返回null
         */
        public String getInput() {
            Optional<String> result = alert.showAndWait();
            if (result.isPresent()) {
                return result.get();
            }
            return null;
        }

        /**
         * AlertUtil.choices
         */
        public <R> R getChoice(R... choices) {
            Optional result = alert.showAndWait();
            return (R) result.get();
        }

        private Stage getStage() {
            return (Stage) alert.getDialogPane().getScene().getWindow();
        }
    }

    public static Builder<Alert> info(String content) {
        return new Builder<Alert>(new Alert(Alert.AlertType.INFORMATION)).content(content).header(null);
    }

    public static Builder<Alert> info() {
        return new Builder<Alert>(new Alert(Alert.AlertType.INFORMATION));
    }

    public static Builder<Alert> error(String message) {
        return new Builder<Alert>(new Alert(Alert.AlertType.ERROR)).header(null).content(message);
    }

    public static Builder<Alert> warning() {
        return new Builder<Alert>(new Alert(Alert.AlertType.WARNING));
    }

    public static Builder<Alert> exception(Exception ex) {
        return new Builder<Alert>(exceptionAlert(ex));
    }

    public static Alert exceptionAlert(Exception ex) {
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
    public static Builder<Alert> confirm() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认对话框");
        return new Builder<Alert>(alert);
    }

    /**
     * 自定义确认对话框 <p>
     * <code>"Cancel"</code> OR <code>"取消"</code> 为取消按钮
     */
    public static Builder<Alert> confirm(String... buttons) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        List<ButtonType> buttonList = Arrays.stream(buttons).map((type) -> {
            ButtonBar.ButtonData buttonData = ButtonBar.ButtonData.OTHER;
            if ("Cancel".equals(type) || "取消".equals(type))
                buttonData = ButtonBar.ButtonData.CANCEL_CLOSE;
            return new ButtonType(type, buttonData);
        }).collect(Collectors.toList());

        alert.getButtonTypes().setAll(buttonList);

        return new Builder<Alert>(alert);
    }

    public static Builder<TextInputDialog> input(String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setContentText(content);
        return new Builder<TextInputDialog>(dialog);
    }

    @SafeVarargs
    public static <T> Builder<ChoiceDialog<T>> choices(String hintText, T... choices) {
        ChoiceDialog<T> dialog = new ChoiceDialog<T>(choices[0], choices);
        dialog.setContentText(hintText);
        return new Builder<ChoiceDialog<T>>(dialog);
    }


    public interface OnChoseListener {
        void confirm();

        void cancelOrClose(ButtonType buttonType);
    }

    public interface OnClickListener {
        void onClicked(String result);
    }
}
