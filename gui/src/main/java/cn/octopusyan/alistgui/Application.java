package cn.octopusyan.alistgui;

import cn.hutool.core.io.FileUtil;
import cn.octopusyan.alistgui.config.Constants;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.manager.ConfigManager;
import cn.octopusyan.alistgui.manager.SystemTrayManager;
import cn.octopusyan.alistgui.manager.http.HttpConfig;
import cn.octopusyan.alistgui.manager.http.HttpUtil;
import cn.octopusyan.alistgui.manager.thread.ThreadPoolManager;
import cn.octopusyan.alistgui.model.upgrade.Gui;
import cn.octopusyan.alistgui.util.ProcessesUtil;
import cn.octopusyan.alistgui.view.alert.AlertUtil;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class Application extends javafx.application.Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    @Getter
    private static Stage primaryStage;

    @Override
    public void init() throws Exception {
        logger.info("application init ...");

        long delay = 0L;
        // 更新重启检查
        File upgradeFile = new File(Constants.DATA_DIR_PATH + File.separator + new Gui().getReleaseFile());
//        logger.error("{}{}{}", Constants.DATA_DIR_PATH, File.separator, new Gui().getReleaseFile());
        if (upgradeFile.exists()) {
            logger.error("upgradeFile.exists");
            FileUtil.del(upgradeFile);
            delay = 1000;
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // 单例模式检查
                makeSingle();
            }
        }, delay);

        // 初始化客户端配置
        ConfigManager.load();

        // http请求工具初始化
        HttpConfig httpConfig = new HttpConfig();
        // 加载代理设置
        switch (ConfigManager.proxySetup()) {
            case NO_PROXY -> httpConfig.setProxySelector(HttpClient.Builder.NO_PROXY);
            case SYSTEM -> httpConfig.setProxySelector(ProxySelector.getDefault());
            case MANUAL -> {
                if (ConfigManager.hasProxy()) {
                    InetSocketAddress unresolved = InetSocketAddress.createUnresolved(
                            Objects.requireNonNull(ConfigManager.proxyHost()),
                            ConfigManager.getProxyPort()
                    );
                    httpConfig.setProxySelector(ProxySelector.of(unresolved));
                }
            }
        }
        httpConfig.setConnectTimeout(3000);
        HttpUtil.init(httpConfig);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        logger.info("application start ...");

        Application.primaryStage = primaryStage;

        Context.setApplication(this);

        // 初始化弹窗工具
        AlertUtil.initOwner(primaryStage);

        //  全局异常处理
        Thread.setDefaultUncaughtExceptionHandler(this::showErrorDialog);
        Thread.currentThread().setUncaughtExceptionHandler(this::showErrorDialog);

        // i18n
        Context.setLanguage(ConfigManager.language());

        // 主题样式
        Application.setUserAgentStylesheet(ConfigManager.theme().getUserAgentStylesheet());

        // 启动主界面
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/assets/logo.png"))));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle(String.format("%s %s", Constants.APP_TITLE, Constants.APP_VERSION));
        Scene scene = Context.initScene();
        primaryStage.setScene(scene);

        primaryStage.show();

        // 静默启动
        if (ConfigManager.silentStartup()) {
            Platform.setImplicitExit(false);
            primaryStage.hide();
            SystemTrayManager.show();
        }

        logger.info("application start over ...");
    }

    private void showErrorDialog(Thread t, Throwable e) {
        logger.error("", e);
        Platform.runLater(() -> AlertUtil.exception(new Exception(e)).show());
    }

    @Override
    public void stop() {
        logger.info("application stop ...");
        // 关闭所有命令
        ProcessesUtil.destroyAll();
        // 保存应用数据
        ConfigManager.save();
        // 停止所有线程
        ThreadPoolManager.getInstance().shutdown();
        // 关闭主界面
        Platform.exit();
        System.exit(0);
    }

    private static final int SINGLE_INSTANCE_LISTENER_PORT = 9009;
    private static final String SINGLE_INSTANCE_FOCUS_MESSAGE = "focus";

    private static final String instanceId = UUID.randomUUID().toString();

    /**
     * 我们在聚焦现有实例之前定义一个暂停
     * 因为有时启动实例的命令行或窗口
     * 可能会在第二个实例执行完成后重新获得焦点
     * 所以我们在聚焦原始窗口之前引入了一个短暂的延迟
     * 以便原始窗口可以保留焦点。
     */
    private static final int FOCUS_REQUEST_PAUSE_MILLIS = 500;

    /**
     * 单实例检测
     *
     * @see <a href='https://www.cnblogs.com/shihaiming/p/13553278.html'>JavaFX单实例运行应用程序</url>
     */
    public static void makeSingle() {
        CountDownLatch instanceCheckLatch = new CountDownLatch(1);

        Thread instanceListener = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(SINGLE_INSTANCE_LISTENER_PORT, 10)) {
                instanceCheckLatch.countDown();

                while (true) {
                    logger.debug(STR."====\{instanceId}====");
                    try (
                            Socket clientSocket = serverSocket.accept();
                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(clientSocket.getInputStream()))
                    ) {
                        String input = in.readLine();
                        logger.info(STR."Received single instance listener message: \{input}");
                        if (input.startsWith(SINGLE_INSTANCE_FOCUS_MESSAGE) && primaryStage != null) {
                            //noinspection BusyWait
                            Thread.sleep(FOCUS_REQUEST_PAUSE_MILLIS);
                            Platform.runLater(() -> {
                                logger.info(STR."To front \{instanceId}");
                                primaryStage.setIconified(false);
                                primaryStage.show();
                                primaryStage.toFront();
                            });
                        }
                    } catch (IOException e) {
                        logger.error("Single instance listener unable to process focus message from client");
                    }
                }
            } catch (java.net.BindException b) {
                logger.error("SingleInstanceApp already running");

                try (
                        Socket clientSocket = new Socket(InetAddress.getLocalHost(), SINGLE_INSTANCE_LISTENER_PORT);
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
                ) {
                    logger.info("Requesting existing app to focus");
                    out.println(STR."\{SINGLE_INSTANCE_FOCUS_MESSAGE} requested by \{instanceId}");
                } catch (IOException e) {
                    logger.error("", e);
                }

                logger.info(STR."Aborting execution for instance \{instanceId}");
                Platform.exit();
            } catch (Exception e) {
                logger.error("", e);
            } finally {
                instanceCheckLatch.countDown();
            }
        }, "instance-listener");
        instanceListener.setDaemon(true);
        instanceListener.start();

        try {
            instanceCheckLatch.await();
        } catch (InterruptedException e) {
            //noinspection ResultOfMethodCallIgnored
            Thread.interrupted();
        }
    }
}