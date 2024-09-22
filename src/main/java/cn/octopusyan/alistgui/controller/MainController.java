package cn.octopusyan.alistgui.controller;

import cn.octopusyan.alistgui.base.BaseController;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.config.I18n;
import cn.octopusyan.alistgui.manager.AListManager;
import cn.octopusyan.alistgui.manager.ConsoleLog;
import cn.octopusyan.alistgui.util.FxmlUtil;
import cn.octopusyan.alistgui.viewModel.MainViewModel;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 主界面控制器
 *
 * @author octopus_yan
 */
public class MainController extends BaseController<MainViewModel> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public VBox mainView;
    public VBox logArea;
    public ScrollPane logAreaSp;

    @I18n(key = "main.status.label-stop")
    public Button statusLabel;

    @I18n(key = "main.control.start")
    public Button startButton;

    @I18n(key = "main.control.password")
    public Button passwordButton;

    @I18n(key = "main.control.restart")
    public Button restartButton;

    @I18n(key = "main.control.more")
    public MenuButton moreButton;

    @I18n(key = "main.more.browser")
    public MenuItem browserButton;

    @I18n(key = "main.more.open-config")
    public MenuItem configButton;

    @I18n(key = "main.more.open-log")
    public MenuItem logButton;

    private PasswordController controller;

    @Override
    public VBox getRootPanel() {
        return mainView;
    }

    @Override
    public void initData() {
        ConsoleLog.init(logAreaSp, logArea);
    }

    @Override
    public void initViewStyle() {
        // 运行状态监听
        viewModel.runningProperty().addListener((_, _, running) -> {
            resetStatus(running);
            browserButton.disableProperty().set(!running);
        });
    }

    @Override
    public void initViewAction() {
    }

    // start button
    public void start() {
        if (AListManager.isRunning()) {
            AListManager.stop();
        } else {
            AListManager.start();
        }
    }

    // password button
    public void adminPassword() throws IOException {
        if (controller == null) {
            FXMLLoader load = FxmlUtil.load("admin-panel");
            load.load();
            controller = load.getController();
        }
        controller.show();
    }

    // restart button
    public void restart() {
        AListManager.restart();
    }

    // more button

    public void openInBrowser() {
        AListManager.openScheme();
    }

    public void openLogFolder() {
        AListManager.openLogFolder();
    }

    public void openConfig() {
        AListManager.openConfig();
    }

    /**
     * 根据运行状态改变按钮样式
     *
     * @param running 运行状态
     */
    private void resetStatus(boolean running) {
        String removeStyle = running ? "success" : "danger";
        String addStyle = running ? "danger" : "success";
        StringBinding button = Context.getLanguageBinding(STR."main.control.\{running ? "stop" : "start"}");
        StringBinding status = Context.getLanguageBinding(STR."main.status.label-\{running ? "running" : "stop"}");

        Platform.runLater(() -> {
            startButton.getStyleClass().remove(removeStyle);
            startButton.getStyleClass().add(addStyle);
            startButton.textProperty().bind(button);

            statusLabel.getStyleClass().remove(addStyle);
            statusLabel.getStyleClass().add(removeStyle);
            statusLabel.textProperty().bind(status);
        });
    }
}
