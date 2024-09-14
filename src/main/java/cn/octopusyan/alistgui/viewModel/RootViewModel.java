package cn.octopusyan.alistgui.viewModel;

import cn.octopusyan.alistgui.base.BaseViewModel;
import cn.octopusyan.alistgui.config.Context;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Root VM
 *
 * @author octopus_yan
 */
public class RootViewModel extends BaseViewModel {
    private final IntegerProperty currentViewIndex = new SimpleIntegerProperty(Context.currentViewIndex()) {
        {
            Context.currentViewIndexProperty().bind(this);
        }
    };

    public IntegerProperty currentViewIndexProperty() {
        return currentViewIndex;
    }
}
