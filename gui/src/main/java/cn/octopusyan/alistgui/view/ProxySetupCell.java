package cn.octopusyan.alistgui.view;

import cn.octopusyan.alistgui.enums.ProxySetup;
import javafx.scene.control.ListCell;

/**
 * ProxySetup I18n Cell
 *
 * @author octopus_yan
 */
public class ProxySetupCell extends ListCell<ProxySetup> {

    @Override
    protected void updateItem(ProxySetup item, boolean empty) {
        super.updateItem(item, empty);
        textProperty().unbind();
        if (empty || item == null) {
            setText("");
        } else {
            textProperty().bind(item.getBinding());
        }
    }
}
