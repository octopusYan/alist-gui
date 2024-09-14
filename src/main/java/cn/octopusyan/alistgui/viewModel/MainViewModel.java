package cn.octopusyan.alistgui.viewModel;

import cn.octopusyan.alistgui.base.BaseViewModel;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.manager.AListManager;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

/**
 * 主界面VM
 *
 * @author octopus_yan
 */
public class MainViewModel extends BaseViewModel {
    private final ListProperty<String> startBtnStyleCss = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final StringProperty startBtnText = new SimpleStringProperty();
    private final ListProperty<String> statusLabelStyleCss = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final StringProperty statusLabelText = new SimpleStringProperty();
    private final BooleanProperty browserButtonDisable = new SimpleBooleanProperty();
    private final BooleanProperty running = new SimpleBooleanProperty();

    public MainViewModel() {
        running.addListener((_, _, running) -> {
            resetStatus(running);
            browserButtonDisable.set(!running);
        });
        // 先添加监听再绑定，解决切换locale后，界面状态显示错误的问题
        running.bind(AListManager.runningProperty());
    }

    public ListProperty<String> startBtnStyleCssProperty() {
        return startBtnStyleCss;
    }

    public StringProperty startBtnTextProperty() {
        return startBtnText;
    }

    public ListProperty<String> statusLabelStyleCssProperty() {
        return statusLabelStyleCss;
    }

    public StringProperty statusLabelTextProperty() {
        return statusLabelText;
    }

    public BooleanProperty browserButtonDisableProperty() {
        return browserButtonDisable;
    }

    public void resetStatus(boolean running) {
        String removeStyle = running ? "success" : "danger";
        String addStyle = running ? "danger" : "success";
        String button = Context.getLanguageBinding(STR."main.control.\{running ? "stop" : "start"}").get();
        String status = Context.getLanguageBinding(STR."main.status.label-\{running ? "running" : "stop"}").get();

        Platform.runLater(() -> {
            startBtnStyleCss.remove(removeStyle);
            startBtnStyleCss.add(addStyle);
            startBtnText.set(button);

            statusLabelStyleCss.remove(addStyle);
            statusLabelStyleCss.add(removeStyle);
            statusLabelText.set(status);
        });
    }
}
