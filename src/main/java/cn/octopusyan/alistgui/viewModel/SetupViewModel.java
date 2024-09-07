package cn.octopusyan.alistgui.viewModel;

import cn.octopusyan.alistgui.base.BaseTask;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.enums.ProxySetup;
import cn.octopusyan.alistgui.manager.ConfigManager;
import cn.octopusyan.alistgui.task.UpgradeTask;
import cn.octopusyan.alistgui.util.alert.AlertUtil;
import javafx.beans.property.*;

import java.util.Locale;

/**
 * 设置视图数据
 *
 * @author octopus_yan
 */
public class SetupViewModel {
    private final BooleanProperty autoStart = new SimpleBooleanProperty(ConfigManager.autoStart());
    private final BooleanProperty silentStartup = new SimpleBooleanProperty(ConfigManager.silentStartup());
    private final StringProperty proxyHost = new SimpleStringProperty(ConfigManager.proxyHost());
    private final StringProperty proxyPort = new SimpleStringProperty(ConfigManager.proxyPort());
    private final ObjectProperty<Locale> language = new SimpleObjectProperty<>(ConfigManager.language());
    private final ObjectProperty<ProxySetup> proxySetup = new SimpleObjectProperty<>(ConfigManager.proxySetup());
    private final StringProperty aListVersion = new SimpleStringProperty(ConfigManager.aListVersion());
    private final StringProperty aListNewVersion = new SimpleStringProperty("");
    private final BooleanProperty aListUpgrade = new SimpleBooleanProperty(false);


    public SetupViewModel() {
        aListVersion.addListener((_, _, newValue) -> ConfigManager.aListVersion(newValue));
        autoStart.addListener((_, _, newValue) -> ConfigManager.autoStart(newValue));
        silentStartup.addListener((_, _, newValue) -> ConfigManager.silentStartup(newValue));
        proxySetup.addListener((_, _, newValue) -> ConfigManager.proxySetup(newValue));
        proxyHost.addListener((_, _, newValue) -> ConfigManager.proxyHost(newValue));
        proxyPort.addListener((_, _, newValue) -> ConfigManager.proxyPort(newValue));
        language.addListener((_, _, newValue) -> Context.setLanguage(newValue));
    }

    public BooleanProperty autoStartProperty() {
        return autoStart;
    }

    public BooleanProperty silentStartupProperty() {
        return silentStartup;
    }

    public ObjectProperty<Locale> languageProperty() {
        return language;
    }

    public ObjectProperty<ProxySetup> proxySetupProperty() {
        return proxySetup;
    }

    public StringProperty proxyHostProperty() {
        return proxyHost;
    }

    public StringProperty proxyPortProperty() {
        return proxyPort;
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

    /**
     * 检查alist更新
     */
    public void checkAListUpdate() {
        var task = new UpgradeTask(this, ConfigManager.aList());
        task.onListen(new BaseTask.Listener() {
            @Override
            public void onSucceeded() {
                AlertUtil.info(STR."""
                    当前版本    :   \{aListVersion.get()}
                    最新版本    :   \{aListNewVersion.get()}
                    """)
                        .title("AList 更新提示")
                        .show();
            }

            @Override
            public void onFailed(Throwable throwable) {
                AlertUtil.exception(new Exception(throwable)).show();
            }
        });
        task.execute();
    }
}
