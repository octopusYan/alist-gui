package cn.octopusyan.alistgui.viewModel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.octopusyan.alistgui.base.BaseTask;
import cn.octopusyan.alistgui.config.Constants;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.manager.ConfigManager;
import cn.octopusyan.alistgui.manager.ConsoleLog;
import cn.octopusyan.alistgui.model.upgrade.AList;
import cn.octopusyan.alistgui.model.upgrade.UpgradeApp;
import cn.octopusyan.alistgui.task.DownloadTask;
import cn.octopusyan.alistgui.task.UpgradeTask;
import cn.octopusyan.alistgui.util.alert.AlertBuilder;
import cn.octopusyan.alistgui.util.alert.AlertUtil;
import javafx.application.Platform;
import javafx.beans.property.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipFile;

/**
 * 关于
 *
 * @author octopus_yan
 */
@Slf4j
public class AboutViewModule {
    private final StringProperty aListVersion = new SimpleStringProperty(ConfigManager.aListVersion());
    private final StringProperty aListNewVersion = new SimpleStringProperty("");
    private final BooleanProperty aListUpgrade = new SimpleBooleanProperty(false);
    private final StringProperty guiVersion = new SimpleStringProperty(ConfigManager.guiVersion());
    private final StringProperty guiNewVersion = new SimpleStringProperty("");
    private final BooleanProperty guiUpgrade = new SimpleBooleanProperty(false);

    public AboutViewModule() {
        aListVersion.addListener((_, _, newValue) -> ConfigManager.aListVersion(newValue));
    }

    public Property<String> aListVersionProperty() {
        return aListVersion;
    }

    public BooleanProperty aListUpgradeProperty() {
        return aListUpgrade;
    }

    public StringProperty aListNewVersionProperty() {
        return aListNewVersion;
    }

    public StringProperty guiVersionProperty() {
        return guiVersion;
    }

    public StringProperty guiNewVersionProperty() {
        return guiNewVersion;
    }

    public BooleanProperty guiUpgradeProperty() {
        return guiUpgrade;
    }

    /**
     * 检查更新
     */
    public void checkUpdate(UpgradeApp app) {

        // 检查任务
        startUpgrade(app, () -> {
            // 判断 检查的应用
            boolean tag = app instanceof AList;

            boolean upgrade = tag ? aListUpgrade.get() : guiUpgrade.get();
            String version = tag ? aListVersion.get() : guiVersion.get();
            String newVersion = tag ? aListNewVersion.get() : guiNewVersion.get();
            String title = Context.getLanguageBinding(STR."about.\{tag ? "alist" : "app"}.update").getValue();
            String currentLabel = Context.getLanguageBinding("update.current").get();
            String newLabel = Context.getLanguageBinding("update.remote").get();
            String header = Context.getLanguageBinding(STR."update.upgrade.\{upgrade ? "new" : "not"}").get();

            String msg = STR."\{app.getRepo()}\{upgrade ? "" : STR." \{version}"} \{header} \{upgrade ? newVersion : ""}";
            log.info(msg);
            ConsoleLog.info(msg);

            AlertBuilder builder = upgrade ? AlertUtil.confirm() : AlertUtil.info();
            builder.title(title)
                    .header(header)
                    .content(STR."""
                                \{currentLabel}    :   \{version}
                                \{newLabel}    :   \{newVersion}
                                """)
                    .show(() -> {
                        if (upgrade) startDownload(app, newVersion);
                    });
        });
    }

    private void startUpgrade(UpgradeApp app, Runnable runnable) {
        // 检查更新的任务
        var task = new UpgradeTask(this, app);

        // 加载弹窗
        final var progress = AlertUtil.progress();
        progress.title(Context.getLanguageBinding("proxy.test.title").get());
        progress.onCancel(task::cancel);

        // 任务监听
        task.onListen(new BaseTask.Listener() {

            @Override
            public void onStart() {
                String msg = STR."start update \{app.getRepo()}...";
                log.info(msg);
                ConsoleLog.info(msg);
            }

            @Override
            public void onRunning() {
                progress.show();
            }

            @Override
            public void onSucceeded() {
                progress.close();
                if (runnable != null) runnable.run();
            }

            @Override
            public void onFailed(Throwable throwable) {
                String msg = STR."\{app.getRepo()} check version error";
                log.error(msg, throwable);
                ConsoleLog.error(STR."\{msg} : \{throwable.getMessage()}");
                AlertUtil.exception(new Exception(throwable)).show();
            }
        });
        // 执行任务
        task.execute();
    }

    /**
     * 下载文件
     *
     * @param app     应用
     * @param version 下载版本
     */
    private void startDownload(UpgradeApp app, String version) {
        DownloadTask downloadTask = new DownloadTask(app.getDownloadUrl(version));
        downloadTask.onListen(new DownloadTask.Listener() {

            private volatile int lastProgress = 0;

            @Override
            public void onStart() {
                String msg = STR."download \{app.getRepo()} start";
                log.info(msg);
                ConsoleLog.info(msg);
            }

            @Override
            public void onProgress(Long total, Long progress) {
                int a = (int) (((double) progress / total) * 100);
                if (a % 10 == 0) {
                    if (a != lastProgress) {
                        lastProgress = a;
                        String msg = STR."\{app.getRepo()} \{a} %";
                        log.info(STR."download \{msg}");
                        ConsoleLog.info(msg);
                    }
                }
            }

            @Override
            public void onSucceeded() {
                String msg = STR."download \{app.getRepo()} success";
                log.info(msg);
                ConsoleLog.info(msg);

                // 解压并删除文件
                unzip(app);

                Platform.runLater(() -> aListVersion.setValue(aListNewVersion.getValue()));
            }

            @Override
            public void onFailed(Throwable throwable) {
                log.error("下载失败", throwable);
                ConsoleLog.error(STR."下载失败 => \{throwable.getMessage()}");
            }
        });
        downloadTask.execute();
    }

    private void unzip(UpgradeApp app) {
        File file = new File(Constants.BIN_DIR_PATH + File.separator + app.getReleaseFile());
        ZipFile zipFile = ZipUtil.toZipFile(file, CharsetUtil.defaultCharset());
        ZipUtil.read(zipFile, zipEntry -> {
            String path = zipEntry.getName();
            if (FileUtil.isWindows()) {
                // Win系统下
                path = StrUtil.replace(path, "*", "_");
            }

            final File outItemFile = FileUtil.file(Constants.BIN_DIR_PATH, path);
            if (zipEntry.isDirectory()) {
                // 目录
                //noinspection ResultOfMethodCallIgnored
                outItemFile.mkdirs();
            } else {
                InputStream in = ZipUtil.getStream(zipFile, zipEntry);
                // 文件
                FileUtil.writeFromStream(in, outItemFile, false);

                log.info(STR."unzip ==> \{outItemFile.getAbsoluteFile()}");
            }
        });

        // 解压完成后删除
        FileUtil.del(file);
    }
}
