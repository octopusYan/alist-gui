package cn.octopusyan.alistgui.util.alert;

import javafx.scene.control.TextInputDialog;
import javafx.stage.Window;

import java.util.Optional;

/**
 * @author octopus_yan
 */
public class TextInputBuilder extends BaseBuilder<TextInputDialog, String>{
    public TextInputBuilder(TextInputDialog alert, Window mOwner) {
        super(alert, mOwner);
    }

    /**
     * AlertUtil.input
     * 如果用户点击了取消按钮,将会返回null
     */
    public String getInput() {
        Optional<String> result = alert.showAndWait();
        return result.orElse(null);
    }
}
