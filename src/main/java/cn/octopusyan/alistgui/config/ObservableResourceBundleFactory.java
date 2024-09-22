package cn.octopusyan.alistgui.config;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

import java.util.ResourceBundle;

/**
 * 多国语言属性绑定
 *
 * @author octopus_yan
 */
@Getter
public class ObservableResourceBundleFactory {

    private final ObjectProperty<ResourceBundle> resourceBundleProperty = new SimpleObjectProperty<>();

    public ResourceBundle getResourceBundle() {
        return getResourceBundleProperty().get();
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        getResourceBundleProperty().set(resourceBundle);
    }

    public StringBinding getStringBinding(String key) {
        return Bindings.createStringBinding(() -> getResourceBundle().getString(key), resourceBundleProperty);
    }
}
