package cn.octopusyan.upgrade.alert.builder;

import cn.octopusyan.upgrade.alert.AlertUtil;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author octopus_yan
 */
public class AlertBuilder extends BaseBuilder<AlertBuilder, Alert> {

    public AlertBuilder(Window owner, Alert.AlertType alertType) {
        super(new Alert(alertType), owner);
    }

    public AlertBuilder buttons(String... buttons) {
        dialog.getButtonTypes().addAll(getButtonList(buttons));
        return this;
    }

    public AlertBuilder buttons(ButtonType... buttons) {
        dialog.getButtonTypes().addAll(buttons);
        return this;
    }

    public AlertBuilder exception(Exception ex) {
        dialog.setTitle("Exception Dialog");
        dialog.setHeaderText(ex.getClass().getSimpleName());
        dialog.setContentText(ex.getMessage());

        // 创建可扩展的异常。
        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        var exceptionText = sw.toString();

        var label = new Label("The exception stacktrace was :");

        var textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        var expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // 将可扩展异常设置到对话框窗格中。
        dialog.getDialogPane().setExpandableContent(expContent);
        return this;
    }

    /**
     * 获取按钮列表
     *
     * @param buttons "Cancel" / "取消" 为取消按钮
     */
    private List<ButtonType> getButtonList(String[] buttons) {
        if (ArrayUtils.isEmpty(buttons)) return Collections.emptyList();

        return Arrays.stream(buttons).map((type) -> {
            ButtonBar.ButtonData buttonData = ButtonBar.ButtonData.OTHER;
            if ("cancel".equals(StringUtils.lowerCase(type)) || "取消".equals(type)) {
                return ButtonType.CANCEL;
            }
            return new ButtonType(type, buttonData);
        }).collect(Collectors.toList());
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