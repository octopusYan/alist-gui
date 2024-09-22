package cn.octopusyan.alistgui.viewModel;

import cn.octopusyan.alistgui.Application;
import cn.octopusyan.alistgui.base.BaseViewModel;
import cn.octopusyan.alistgui.config.Constants;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.manager.AListManager;
import cn.octopusyan.alistgui.manager.ConfigManager;
import cn.octopusyan.alistgui.manager.ConsoleLog;
import cn.octopusyan.alistgui.model.upgrade.AList;
import cn.octopusyan.alistgui.model.upgrade.Gui;
import cn.octopusyan.alistgui.model.upgrade.UpgradeApp;
import cn.octopusyan.alistgui.task.CheckUpdateTask;
import cn.octopusyan.alistgui.task.listener.TaskListener;
import cn.octopusyan.alistgui.util.DownloadUtil;
import cn.octopusyan.alistgui.util.ProcessesUtil;
import cn.octopusyan.alistgui.view.alert.AlertUtil;
import cn.octopusyan.alistgui.view.alert.builder.AlertBuilder;
import javafx.application.Platform;
import javafx.beans.property.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 关于
 *
 * @author octopus_yan
 */
@Slf4j
public class AboutViewModule extends BaseViewModel {
    private final StringProperty aListVersion = new SimpleStringProperty(ConfigManager.aListVersion());
    private final StringProperty aListNewVersion = new SimpleStringProperty("");
    private final BooleanProperty aListUpgrade = new SimpleBooleanProperty(false);
    private final StringProperty guiVersion = new SimpleStringProperty(ConfigManager.guiVersion());
    private final StringProperty guiNewVersion = new SimpleStringProperty("");
    private final BooleanProperty guiUpgrade = new SimpleBooleanProperty(false);

    public AboutViewModule() {
        aListVersion.bindBidirectional(ConfigManager.aListVersionProperty());
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
        startUpgrade(app, () -> onChecked(app));
    }

    /**
     * 开始检查更新
     *
     * @param app      更新的应用
     * @param runnable 检查后执行的任务
     */
    private void startUpgrade(UpgradeApp app, Runnable runnable) {
        // 检查更新的任务
        var task = new CheckUpdateTask(app);

        // 任务监听
        task.onListen(new TaskListener.UpgradeListener(task) {

            @Override
            protected void onSucceed() {
                if (runnable != null) runnable.run();
            }

            @Override
            public void onChecked(boolean hasUpgrade, String version) {
                // 版本检查结果
                Platform.runLater(() -> {
                    if (app instanceof AList) {
                        aListUpgrade.setValue(hasUpgrade);
                        aListNewVersion.setValue(version);
                    } else {
                        guiUpgrade.setValue(hasUpgrade);
                        guiNewVersion.setValue(version);
                    }
                });
            }

            @Override
            protected void onFail(Throwable throwable) {
                AlertUtil.exception(new Exception(throwable)).show();
            }
        });
        // 执行任务
        task.execute();
    }


    private void onChecked(UpgradeApp app) {
        // 判断 检查的应用
        boolean tag = app instanceof AList;

        boolean upgrade = tag ? aListUpgrade.get() : guiUpgrade.get();
        String version = tag ? aListVersion.get() : guiVersion.get();
        String newVersion = tag ? aListNewVersion.get() : guiNewVersion.get();
        String title = Context.getLanguageBinding(STR."about.\{tag ? "alist" : "app"}.update").getValue();
        String currentLabel = Context.getLanguageBinding("update.current").get();
        String newLabel = Context.getLanguageBinding("update.remote").get();
        String header = Context.getLanguageBinding(STR."update.upgrade.\{upgrade ? "new" : "not"}").get();

        // 版本检查消息
        String msg = STR."\{app.getRepo()}\{upgrade ? "" : STR." \{version}"} \{header} \{upgrade ? newVersion : ""}";
        log.info(msg);
        ConsoleLog.info(msg);

        // 弹窗
        AlertBuilder builder = upgrade ? AlertUtil.confirm() : AlertUtil.info();
        builder.title(title)
                .header(header)
                .content(STR."""
                                \{currentLabel}    :   \{version}
                                \{newLabel}    :   \{newVersion}
                                """)
                .show(() -> {
                    // 可升级，且点击了确定后，开始下载任务
                    if (upgrade)
                        DownloadUtil.startDownload(app, newVersion, () -> {

                            // 下载完成后，解压并删除文件
                            DownloadUtil.unzip(app);

                            // 解压后
                            Platform.runLater(() -> {
                                switch (app) {
                                    case AList _ -> {
                                        // 设置应用版本
                                        aListVersion.setValue(aListNewVersion.getValue());
                                        AListManager.restart();
                                    }
                                    case Gui _ -> {
                                        log.info(STR."guiNewVersion => \{guiNewVersion.get()}");
                                        // 重启
                                        Platform.setImplicitExit(true);
                                        Application.getPrimaryStage().close();

                                        ProcessesUtil.init(Constants.DATA_DIR_PATH).exec(STR."\{Constants.APP_NAME}.exe");
                                    }
                                    default -> throw new IllegalStateException(STR."Unexpected value: \{app}");
                                }
                            });

                        }).execute();
                });
    }
}
