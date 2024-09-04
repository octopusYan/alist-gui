package cn.octopusyan.alistgui.controller;

import cn.octopusyan.alistgui.base.BaseController;
import javafx.fxml.FXML;
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

    @Override
    public VBox getRootPanel() {
        return mainView;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initViewStyle() {

    }

    @Override
    public void initViewAction() {

    }
}
