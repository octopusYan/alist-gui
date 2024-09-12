package cn.octopusyan.alistgui.controller;

import cn.octopusyan.alistgui.base.BaseController;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.manager.AListManager;
import cn.octopusyan.alistgui.manager.ConsoleLog;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 主界面控制器
 *
 * @author octopus_yan
 */
public class MainController extends BaseController<VBox> {
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
        AListManager.runningProperty().addListener(_ -> setStartButton(AListManager.isRunning()));
    }

    @Override
    public void initViewAction() {
        AListManager.runningProperty().addListener((_, _, running) -> setStartButton(running));
    }

    private void setStartButton(boolean running) {
        String removeStyle = running ? "success" : "danger";
        String addStyle = running ? "danger" : "success";
        String button = Context.getLanguageBinding(STR."main.control.\{running ? "stop" : "start"}").get();
        String status = Context.getLanguageBinding(STR."main.status.label-\{running ? "running" : "stop"}").get();

        Platform.runLater(() -> {
            startButton.getStyleClass().remove(removeStyle);
            startButton.getStyleClass().add(addStyle);
            startButton.textProperty().set(button);

            statusLabel.getStyleClass().remove(addStyle);
            statusLabel.getStyleClass().add(removeStyle);
            statusLabel.textProperty().set(status);
        });
    }

    @FXML
    public void start() {
        if (AListManager.isRunning()) {
            AListManager.stop();
        } else {
            AListManager.start();
        }
    }

    @FXML
    public void restart() {
        AListManager.restart();
    }
}
