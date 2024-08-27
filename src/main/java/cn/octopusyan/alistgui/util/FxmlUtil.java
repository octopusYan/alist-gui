package cn.octopusyan.alistgui.util;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;

import java.nio.charset.StandardCharsets;

/**
 * FXML 工具
 *
 * @author octopus_yan@foxmail.com
 */
public class FxmlUtil {

    public static FXMLLoader load(String name) {
        String prefix = "/fxml/";
        String suffix = ".fxml";
        return new FXMLLoader(
                FxmlUtil.class.getResource(prefix + name + suffix),
                null,
                new JavaFXBuilderFactory(),
                null,
                StandardCharsets.UTF_8
        );
    }
}
