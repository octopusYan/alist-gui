package cn.octopusyan.alistgui.viewModel;

import cn.octopusyan.alistgui.base.BaseViewModel;
import cn.octopusyan.alistgui.manager.AListManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * 主界面VM
 *
 * @author octopus_yan
 */
public class MainViewModel extends BaseViewModel {
    private final BooleanProperty running = new SimpleBooleanProperty();

    public MainViewModel() {
        // 先添加监听再绑定，解决切换locale后，界面状态显示错误的问题
        running.bind(AListManager.runningProperty());
    }

    public BooleanProperty runningProperty() {
        return running;
    }
}
