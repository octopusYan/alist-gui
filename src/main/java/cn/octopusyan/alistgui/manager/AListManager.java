package cn.octopusyan.alistgui.manager;

import cn.octopusyan.alistgui.config.Constants;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.model.AListConfig;
import cn.octopusyan.alistgui.task.CheckUpdateTask;
import cn.octopusyan.alistgui.task.DownloadTask;
import cn.octopusyan.alistgui.task.listener.TaskListener;
import cn.octopusyan.alistgui.util.DownloadUtil;
import cn.octopusyan.alistgui.util.ProcessesUtil;
import cn.octopusyan.alistgui.view.alert.AlertUtil;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * AList 管理
 *
 * @author octopus_yan
 */
@Slf4j
public class AListManager {
    public static final String DATA_DIR = STR."\{Constants.BIN_DIR_PATH}\{File.separator}data";
    public static final String LOG_DIR = STR."\{DATA_DIR}\{File.separator}log";
    public static final String CONFIG_FILE = STR."\{DATA_DIR}\{File.separator}config.json";

    public static final String START_COMMAND = STR."\{Constants.ALIST_FILE} server";
    public static final String PWD_SET_COMMAND = STR."\{Constants.ALIST_FILE} admin set";
    public static final String PWD_RANDOM_COMMAND = STR."\{Constants.ALIST_FILE} admin random";

    public static final String DEFAULT_SCHEME = "0.0.0.0:5244";
    public static final String PASSWORD_MSG_REG = ".*password( is)?: (.*)$";
    public static AListConfig aListConfig;

    public static final File configFile = new File(CONFIG_FILE);

    private static final ProcessesUtil util;
    private static final BooleanProperty running = new SimpleBooleanProperty(false);
    private static final StringProperty password = new SimpleStringProperty("******");
    private static DownloadTask downloadTask;

    private static final ProcessesUtil.OnExecuteListener runningListener;

    static {
        util = ProcessesUtil.init(Constants.BIN_DIR_PATH);
        loadConfig();
        runningListener = new ProcessesUtil.OnExecuteListener() {
            @Override
            public void onExecute(String msg) {
                if (hasConfig() && aListConfig == null) loadConfig();

                if (msg.contains("start HTTP server")) {
                    Platform.runLater(() -> running.set(true));
                }

                ConsoleLog.msg(msg);
            }

            @Override
            public void onExecuteSuccess(boolean success) {
                Platform.runLater(() -> running.set(false));
            }

            @Override
            public void onExecuteError(Exception e) {
                Platform.runLater(() -> running.set(false));
                log.error("AList error", e);
                ConsoleLog.error("AList", e.getMessage());
            }
        };
    }

//==============================={ Property }====================================

    private static void loadConfig() {
        if (hasConfig()) {
            aListConfig = ConfigManager.loadConfig(CONFIG_FILE, AListConfig.class);
        }
    }

    public static BooleanProperty runningProperty() {
        return running;
    }

    public static boolean isRunning() {
        return running.get();
    }

    public static boolean hasConfig() {
        return configFile.exists() && aListConfig != null;
    }

    public static String scheme() {
        return hasConfig() ?
                STR."\{aListConfig.getScheme().getAddress()}:\{aListConfig.getScheme().getHttpPort()}"
                : DEFAULT_SCHEME;
    }

    public static StringProperty passwordProperty() {
        return password;
    }

    public static String password() {
        return password.get();
    }

//================================{ action }====================================

    public static void openConfig() {
        Context.openFile(new File(CONFIG_FILE));
    }

    public static void openLogFolder() {
        Context.openFolder(new File(LOG_DIR));
    }

    public static void openScheme() {
        Context.openUrl(STR."http://\{scheme()}");
    }

    public static void start() {
        if (!checkAList()) return;

        if (running.get() || util.isRunning()) {
            ConsoleLog.warning(getText("alist.status.start.running"));
            return;
        }

        ConsoleLog.info(getText("alist.status.start"));

        loadConfig();
        util.exec(START_COMMAND, runningListener);
    }

    public static void stop() {
        ConsoleLog.info(getText("alist.status.stop"));
        if (!running.get()) {
            ConsoleLog.warning(getText("alist.status.stop.stopped"));
            return;
        }
        util.destroy();
    }

    static ChangeListener<Boolean> restartListener;

    public static void restart() {
        if (!running.get()) {
            start();
        } else {
            stop();

            restartListener = (_, _, run) -> {
                if (run) return;
                running.removeListener(restartListener);
                start();
            };
            running.addListener(restartListener);
        }
    }

    public static void resetPassword() {
        resetPassword("");
    }

    static ChangeListener<Boolean> resetPasswordListener;

    public static void resetPassword(String pwd) {
        String command = StringUtils.isNoneEmpty(pwd) ?
                STR."\{PWD_SET_COMMAND} \{pwd}" : PWD_RANDOM_COMMAND;

        if (isRunning()) {
            util.exec(command, ConsoleLog::msg);
            return;
        }

        start();
        resetPasswordListener = (_, _, newValue) -> {
            if (newValue) {
                running.removeListener(resetPasswordListener);
                util.exec(command, ConsoleLog::msg);
            }
        };
        running.addListener(resetPasswordListener);

    }

//============================={ private }====================================

    /**
     * TODO 点击开始时检查 aList 执行文件
     */
    private static boolean checkAList() {
        if (new File(Constants.ALIST_FILE).exists()) return true;

        if (downloadTask != null && downloadTask.isRunning()) {
            ConsoleLog.warning("AList Downloading ...");
            return false;
        }

        var task = new CheckUpdateTask(ConfigManager.aList());
        task.onListen(new TaskListener.UpgradeUpgradeListener(task) {
            @Override
            public void onChecked(boolean hasUpgrade, String version) {
                Platform.runLater(() -> showDownload(version));
            }
        });
        task.execute();

        return false;
    }

    private static void showDownload(String version) {
        String content = STR."""
                \{getText("msg.alist.download.notfile")}
                \{Context.getLanguageBinding("update.remote").get()} : \{version}
                """;
        downloadTask = DownloadUtil.startDownload(ConfigManager.aList(), version, () -> {
            DownloadUtil.unzip(ConfigManager.aList());
            Platform.runLater(() -> ConfigManager.aListVersion(version));
            restart();
        });
        AlertUtil.confirm()
                .title("Download ALst")
                .header(null)
                .content(content)
                .show(downloadTask::execute);
    }

    private static String getText(String code) {
        return Context.getLanguageBinding(code).get();
    }

    public static void tmpPassword(String pwd) {
        Platform.runLater(() -> password.set(pwd));
    }
}
