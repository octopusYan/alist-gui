package cn.octopusyan.alistgui.util.alert;

import javafx.scene.control.TextInputDialog;
import javafx.stage.Window;

import java.util.Optional;

/**
 * 获取用户输入弹窗
 *
 * @author octopus_yan
 */
public class TextInputBuilder extends BaseBuilder<TextInputBuilder, TextInputDialog> {

    public TextInputBuilder(Window mOwner) {
        this(new TextInputDialog(), mOwner);
    }

    public TextInputBuilder(Window mOwner, String defaultResult) {
        this(new TextInputDialog(defaultResult), mOwner);
    }

    public TextInputBuilder(TextInputDialog dialog, Window mOwner) {
        super(dialog, mOwner);
    }

    /**
     * AlertUtil.input
     * 如果用户点击了取消按钮,将会返回null
     */
    public String getInput() {
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
}
