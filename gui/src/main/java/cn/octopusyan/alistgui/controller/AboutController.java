package cn.octopusyan.alistgui.controller;

import cn.octopusyan.alistgui.base.BaseController;
import cn.octopusyan.alistgui.manager.ConfigManager;
import cn.octopusyan.alistgui.viewModel.AboutViewModule;
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
public class AboutController extends BaseController<AboutViewModule> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public VBox aboutView;

    public Label aListVersion;

    public Label aListVersionLabel;

    public Label appVersionLabel;

    public Button checkAppVersion;

    public Button checkAListVersion;

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

    public void checkAListUpdate() {
        viewModel.checkUpdate(ConfigManager.aList());
    }

    public void checkGuiUpdate() {
        viewModel.checkUpdate(ConfigManager.gui());
    }

}
