package cn.octopusyan.alistgui.controller;

import cn.octopusyan.alistgui.base.BaseController;
import cn.octopusyan.alistgui.viewModel.AboutViewModule;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 关于
 *
 * @author octopus_yan
 */
public class AboutController extends BaseController<VBox> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @FXML
    public VBox aboutView;

    @FXML
    public Label aListVersion;
    @FXML
    public Button checkAppVersion;

    private final AboutViewModule viewModule = new AboutViewModule();

    @Override
    public VBox getRootPanel() {
        return aboutView;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initViewStyle() {

    }

    @Override
    public void initViewAction() {
        aListVersion.textProperty().bindBidirectional(viewModule.aListVersionProperty());
    }

    @FXML
    public void checkAListUpdate() {
        viewModule.checkAListUpdate();
    }
}
