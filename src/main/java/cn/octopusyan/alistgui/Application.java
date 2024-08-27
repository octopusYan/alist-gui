package cn.octopusyan.alistgui;

import cn.octopusyan.alistgui.config.AppConstant;
import cn.octopusyan.alistgui.config.CustomConfig;
import cn.octopusyan.alistgui.controller.MainController;
import cn.octopusyan.alistgui.manager.http.HttpConfig;
import cn.octopusyan.alistgui.manager.http.HttpUtil;
import cn.octopusyan.alistgui.manager.thread.ThreadPoolManager;
import cn.octopusyan.alistgui.util.AlertUtil;
import cn.octopusyan.alistgui.util.FxmlUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.util.Objects;

public class Application extends javafx.application.Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Override
    public void init() throws Exception {
        logger.info("application init ...");
        // 初始化客户端配置
        CustomConfig.init();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        logger.info("application start ...");

        // 初始化弹窗工具
        AlertUtil.initOwner(primaryStage);

        // http请求工具初始化
        HttpConfig httpConfig = new HttpConfig();
        if (CustomConfig.hasProxy()) {
            InetSocketAddress unresolved = InetSocketAddress.createUnresolved(CustomConfig.proxyHost(), CustomConfig.proxyPort());
            httpConfig.setProxySelector(ProxySelector.of(unresolved));
        }
        httpConfig.setConnectTimeout(10);
        HttpUtil.init(httpConfig);

        //  全局异常处理
        Thread.setDefaultUncaughtExceptionHandler(this::showErrorDialog);
        Thread.currentThread().setUncaughtExceptionHandler(this::showErrorDialog);

        // 启动主界面
        try {
            FXMLLoader loader = FxmlUtil.load("root-view");
            loader.setControllerFactory(c -> new MainController(primaryStage));
            Parent root = loader.load();//底层面板

            Scene scene = new Scene(root);
            scene.getStylesheets().addAll(Objects.requireNonNull(getClass().getResource("/css/root.css")).toExternalForm());
            scene.setFill(Color.TRANSPARENT);

            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setTitle(String.format("%s v%s", AppConstant.APP_TITLE, AppConstant.APP_VERSION));
            primaryStage.show();

            MainController controller = loader.getController();
            controller.setApplication(this);
        } catch (Throwable t) {
            showErrorDialog(Thread.currentThread(), t);
        }

        logger.info("application start over ...");
    }

    private void showErrorDialog(Thread t, Throwable e) {
        logger.error("", e);
        AlertUtil.exceptionAlert(new Exception(e)).show();
    }

    @Override
    public void stop() throws Exception {
        logger.info("application stop ...");
        // 停止所有线程
        ThreadPoolManager.getInstance().shutdown();
        // 保存应用数据
        CustomConfig.store();
    }
}