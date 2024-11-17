package cn.octopusyan.upgrade.util;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;

import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class FxmlUtil {
    public static FXMLLoader init(String path) {
        return new FXMLLoader(
                FxmlUtil.class.getResource(path),
                ResourceBundle.getBundle("language/language"),
                new JavaFXBuilderFactory(),
                null,
                StandardCharsets.UTF_8
        );
    }
}
