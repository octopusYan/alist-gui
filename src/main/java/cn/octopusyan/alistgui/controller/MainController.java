package cn.octopusyan.alistgui.controller;

import cn.octopusyan.alistgui.base.BaseController;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * 主页面控制器
 *
 * @author octopus_yan@foxmail.com
 */
public class MainController extends BaseController<VBox> implements Initializable {

    private double xOffset;
    private double yOffset;

    // 布局
    @FXML
    public VBox rootPane;
    @FXML
    public HBox windowHeader;
    @FXML
    public Button alwaysOnTopIcon;
    @FXML
    public Button minimizeIcon;
    @FXML
    public Button closeIcon;

    // 界面
    @FXML
    public TabPane tabPane;

    public MainController(Stage primaryStage) {
        super(primaryStage);
    }

    /**
     * 窗口拖拽设置
     *
     * @return 是否启用
     */
    @Override
    public boolean dragWindow() {
        return false;
    }

    /**
     * 获取根布局
     *
     * @return 根布局对象
     */
    @Override
    public VBox getRootPanel() {
        return rootPane;
    }

    /**
     * 初始化数据
     */
    @Override
    public void initData() {

    }

    /**
     * 视图样式
     */
    @Override
    public void initViewStyle() {

    }

    /**
     * 视图事件
     */
    @Override
    public void initViewAction() {
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> onDestroy());
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> ((Stage) rootPane.getScene().getWindow()).setIconified(true));
        alwaysOnTopIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            boolean newVal = !getPrimaryStage().isAlwaysOnTop();
            alwaysOnTopIcon.pseudoClassStateChanged(PseudoClass.getPseudoClass("always-on-top"), newVal);
            getPrimaryStage().setAlwaysOnTop(newVal);
        });

        windowHeader.setOnMousePressed(event -> {
            xOffset = getPrimaryStage().getX() - event.getScreenX();
            yOffset = getPrimaryStage().getY() - event.getScreenY();
        });
        windowHeader.setOnMouseDragged(event -> {
            getPrimaryStage().setX(event.getScreenX() + xOffset);
            getPrimaryStage().setY(event.getScreenY() + yOffset);
        });
    }
}
