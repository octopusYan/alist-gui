package cn.octopusyan.alistgui.util;

import cn.octopusyan.alistgui.config.Context;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;

import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * FXML 工具
 *
 * @author octopus_yan@foxmail.com
 */
public class FxmlUtil {

    public static FXMLLoader load(String name) {
        return load(name, Context.getLanguageResource().get());
    }

    public static FXMLLoader load(String name, ResourceBundle bundle) {
        String prefix = "/fxml/";
        String suffix = ".fxml";
        return new FXMLLoader(
                FxmlUtil.class.getResource(prefix + name + suffix),
                bundle,
                new JavaFXBuilderFactory(),
                Context.getControlFactory(),
                StandardCharsets.UTF_8
        );
    }
}
