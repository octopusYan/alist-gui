package cn.octopusyan.alistgui.controller;

import cn.octopusyan.alistgui.base.BaseController;
import cn.octopusyan.alistgui.config.I18n;
import cn.octopusyan.alistgui.manager.ConfigManager;
import cn.octopusyan.alistgui.view.alert.AlertUtil;
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

    @I18n(key = "about.alist.version")
    public Label aListVersionLabel;

    @I18n(key = "about.app.version")
    public Label appVersionLabel;

    @I18n(key = "about.app.update")
    public Button checkAppVersion;

    @I18n(key = "about.alist.update")
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
        // TODO 检查 gui 版本
        AlertUtil.info("待开发。。。").show();
    }

}
