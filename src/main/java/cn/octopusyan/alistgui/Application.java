package cn.octopusyan.alistgui;

import atlantafx.base.theme.PrimerLight;
import cn.octopusyan.alistgui.config.AppConstant;
import cn.octopusyan.alistgui.config.ConfigManager;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.enums.ProxySetup;
import cn.octopusyan.alistgui.manager.http.HttpConfig;
import cn.octopusyan.alistgui.manager.http.HttpUtil;
import cn.octopusyan.alistgui.manager.thread.ThreadPoolManager;
import cn.octopusyan.alistgui.util.AlertUtil;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;

public class Application extends javafx.application.Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    @Getter
    private static Stage primaryStage;

    @Override
    public void init() throws Exception {
        logger.info("application init ...");
        // 初始化客户端配置
        ConfigManager.load();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Application.primaryStage = primaryStage;

        logger.info("application start ...");

        // 初始化弹窗工具
        AlertUtil.initOwner(primaryStage);

        //  全局异常处理
        Thread.setDefaultUncaughtExceptionHandler(this::showErrorDialog);
        Thread.currentThread().setUncaughtExceptionHandler(this::showErrorDialog);

        // http请求工具初始化
        HttpConfig httpConfig = new HttpConfig();

        if(!ProxySetup.NO_PROXY.equals(ConfigManager.proxySetup())) {
            // 系统代理
            if (ProxySetup.SYSTEM.equals(ConfigManager.proxySetup())) {
                httpConfig.setProxySelector(ProxySelector.getDefault());
            }
            // 自定义代理
            if (ProxySetup.MANUAL.equals(ConfigManager.proxySetup()) && ConfigManager.hasProxy()) {
                InetSocketAddress unresolved = InetSocketAddress.createUnresolved(ConfigManager.proxyHost(), ConfigManager.getProxyPort());
                httpConfig.setProxySelector(ProxySelector.of(unresolved));
            }
        }

        httpConfig.setConnectTimeout(10);
        HttpUtil.init(httpConfig);

        // i18n
        Context.setLanguage(ConfigManager.language());

        // 主题样式
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        // 启动主界面
        Scene scene = Context.initScene();
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle(String.format("%s v%s", AppConstant.APP_TITLE, AppConstant.APP_VERSION));
        primaryStage.show();

        logger.info("application start over ...");
    }

    private void showErrorDialog(Thread t, Throwable e) {
        logger.error("", e);
        AlertUtil.exceptionAlert(new Exception(e)).show();
    }

    @Override
    public void stop() throws Exception {
        logger.info("application stop ...");
        // 保存应用数据
        ConfigManager.save();
        // 停止所有线程
        ThreadPoolManager.getInstance().shutdown();
        // 关闭主界面
        Platform.exit();
        System.exit(0);
    }
}