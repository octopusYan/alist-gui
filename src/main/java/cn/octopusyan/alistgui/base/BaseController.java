package cn.octopusyan.alistgui.base;

import cn.octopusyan.alistgui.config.AppConstant;
import cn.octopusyan.alistgui.util.FxmlUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * 通用视图控制器基类
 *
 * @author octopus_yan@foxmail.com
 */
public abstract class BaseController<P extends Pane> implements Initializable {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Application application;
    private final Stage primaryStage;

    private double xOffSet = 0, yOffSet = 0;

    protected BaseController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void jumpTo(BaseController<P> controller) throws IOException {
        FXMLLoader fxmlLoader = FxmlUtil.load(controller.getRootFxml());

        Scene scene = getRootPanel().getScene();
        double oldHeight = getRootPanel().getPrefHeight();
        double oldWidth = getRootPanel().getPrefWidth();

        Pane root = fxmlLoader.load();
        Stage stage = (Stage) scene.getWindow();
        // 窗口大小
        double newWidth = root.getPrefWidth();
        double newHeight = root.getPrefHeight();
        // 窗口位置
        double newX = stage.getX() - (newWidth - oldWidth) / 2;
        double newY = stage.getY() - (newHeight - oldHeight) / 2;
        scene.setRoot(root);
        stage.setX(newX < 0 ? 0 : newX);
        stage.setY(newY < 0 ? 0 : newY);
        stage.setWidth(newWidth);
        stage.setHeight(newHeight);

        controller = fxmlLoader.getController();
        controller.setApplication(getApplication());
    }

    protected void open(Class<? extends BaseController<?>> clazz, String title) {
        try {
            FXMLLoader load = FxmlUtil.load(clazz.getDeclaredConstructor().newInstance().getRootFxml());
            Parent root = load.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().addAll(Objects.requireNonNull(getClass().getResource("/css/root.css")).toExternalForm());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.initOwner(getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
            load.getController();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

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
                Stage stage = (Stage) getWindow();
                stage.setX(event.getScreenX() - xOffSet);
                stage.setY(event.getScreenY() - yOffSet);
            });
        }

        // 窗口初始化完成监听
        getRootPanel().sceneProperty().addListener((observable, oldValue, newValue) -> {
            newValue.windowProperty().addListener(new ChangeListener<Window>() {
                @Override
                public void changed(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
                    //关闭窗口监听
                    getWindow().setOnCloseRequest(windowEvent -> onDestroy());

                    // app 版本信息
                    if (getAppVersionLabel() != null) getAppVersionLabel().setText("v" + AppConstant.APP_VERSION);

                    // 初始化数据
                    initData();

                    // 初始化视图样式
                    initViewStyle();

                    // 初始化视图事件
                    initViewAction();
                }
            });
        });
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Application getApplication() {
        return application;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * 窗口拖拽设置
     *
     * @return 是否启用
     */
    public abstract boolean dragWindow();

    /**
     * 获取根布局
     *
     * @return 根布局对象
     */
    public abstract P getRootPanel();

    /**
     * 获取根布局
     * <p> 搭配 <code>FxmlUtil.load</code> 使用
     *
     * @return 根布局对象
     * @see FxmlUtil#load(String)
     */
    protected String getRootFxml() {
        System.out.println(getClass().getSimpleName());
        return "";
    }

    protected Window getWindow() {
        return getRootPanel().getScene().getWindow();
    }

    /**
     * App版本信息标签
     */
    public Label getAppVersionLabel() {
        return null;
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

    /**
     * 关闭窗口
     */
    public void onDestroy() {
        Stage stage = (Stage) getWindow();
        stage.hide();
        stage.close();
        try {
            Thread.sleep(1000);
            Platform.exit();
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }
}
