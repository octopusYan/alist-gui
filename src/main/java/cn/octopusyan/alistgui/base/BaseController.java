package cn.octopusyan.alistgui.base;

import cn.octopusyan.alistgui.Application;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.util.FxmlUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 通用视图控制器基类
 *
 * @author octopus_yan@foxmail.com
 */
public abstract class BaseController<P extends Pane> implements Initializable {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private double xOffSet = 0, yOffSet = 0;

    public BaseController() {
        //初始化时保存当前Controller实例
        Context.getControllers().put(this.getClass().getSimpleName(), this);
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 全局窗口拖拽
        if (dragWindow()) {
            // 窗口拖拽
            getRootPanel().setOnMousePressed(event -> {
                xOffSet = event.getSceneX();
                yOffSet = event.getSceneY();
            });
            getRootPanel().setOnMouseDragged(event -> {
                getWindow().setX(event.getScreenX() - xOffSet);
                getWindow().setY(event.getScreenY() - yOffSet);
            });
        }

        // 初始化数据
        initData();

        // 初始化视图样式
        initViewStyle();

        // 初始化视图事件
        initViewAction();
    }

    /**
     * 窗口拖拽设置
     *
     * @return 是否启用
     */
    public boolean dragWindow() {
        return false;
    }

    /**
     * 获取根布局
     *
     * @return 根布局对象
     */
    public abstract P getRootPanel();

    /**
     * 获取根布局
     * <p> 搭配 {@link FxmlUtil#load(String)} 使用
     *
     * @return 根布局对象
     */
    protected String getRootFxml() {
        System.out.println(getClass().getSimpleName());
        return "";
    }

    protected Stage getWindow() {
        return Application.getPrimaryStage();
    }

    /**
     * 初始化数据
     */
    public abstract void initData();

    /**
     * 视图样式
     */
    public abstract void initViewStyle();

    /**
     * 视图事件
     */
    public abstract void initViewAction();
}
