package cn.octopusyan.alistgui.viewModel;

import cn.octopusyan.alistgui.base.BaseViewModel;
import cn.octopusyan.alistgui.manager.AListManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Admin Panel VM
 *
 * @author octopus_yan
 */
public class AdminPanelViewModel extends BaseViewModel {
    private final StringProperty password = new SimpleStringProperty(AListManager.passwordProperty().get());

    public AdminPanelViewModel() {
        AListManager.passwordProperty().subscribe(password::set);
    }

    public StringProperty passwordProperty() {
        return password;
    }
}
