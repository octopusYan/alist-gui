package cn.octopusyan.alistgui.controller;

import cn.octopusyan.alistgui.base.BaseController;
import cn.octopusyan.alistgui.manager.ConfigManager;
import cn.octopusyan.alistgui.view.alert.AlertUtil;
import cn.octopusyan.alistgui.viewModel.AboutViewModule;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 关于
 *
 * @author octopus_yan
 */
public class AboutController extends BaseController<AboutViewModule> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @FXML
    public VBox aboutView;

    @FXML
    public Label aListVersion;

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
        aListVersion.textProperty().bindBidirectional(viewModel.aListVersionProperty());
    }

    @FXML
    public void checkAListUpdate() {
        viewModel.checkUpdate(ConfigManager.aList());
    }

    @FXML
    public void checkGuiUpdate() {
        // TODO 检查 gui 版本
        AlertUtil.info("待开发。。。").show();
    }

}
