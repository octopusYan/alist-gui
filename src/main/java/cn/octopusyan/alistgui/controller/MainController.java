package cn.octopusyan.alistgui.controller;

import cn.octopusyan.alistgui.base.BaseController;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.manager.AListManager;
import cn.octopusyan.alistgui.manager.ConsoleLog;
import cn.octopusyan.alistgui.util.FxmlUtil;
import cn.octopusyan.alistgui.viewModel.MainViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
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

    @FXML
    public VBox mainView;
    @FXML
    public VBox logArea;
    @FXML
    public ScrollPane logAreaSp;
    @FXML
    public Button statusLabel;
    @FXML
    public Button startButton;
    @FXML
    public MenuItem browserButton;

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
    }

    @Override
    public void initViewAction() {
        viewModel.startBtnStyleCssProperty().bindContentBidirectional(startButton.getStyleClass());
        viewModel.statusLabelStyleCssProperty().bindContentBidirectional(statusLabel.getStyleClass());
        viewModel.startBtnTextProperty().bindBidirectional(startButton.textProperty());
        viewModel.statusLabelTextProperty().bindBidirectional(statusLabel.textProperty());
        viewModel.browserButtonDisableProperty().bindBidirectional(browserButton.disableProperty());
    }

    // start button
    @FXML
    public void start() {
        if (AListManager.isRunning()) {
            AListManager.stop();
        } else {
            AListManager.start();
        }
    }

    // password button
    @FXML
    public void adminPassword() throws IOException {
        if (controller == null) {
            FXMLLoader load = FxmlUtil.load("admin-panel");
            load.load();
            controller = load.getController();
        }
        controller.show();
    }

    // restart button
    @FXML
    public void restart() {
        AListManager.restart();
    }

    // more button

    @FXML
    public void openInBrowser() {
        AListManager.openScheme();
    }

    @FXML
    public void openLogFolder() {
        AListManager.openLogFolder();
    }

    @FXML
    public void openConfig() {
        AListManager.openConfig();
    }

    private String getText(String key) {
        return Context.getLanguageBinding(key).get();
    }
}
