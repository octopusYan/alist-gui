package cn.octopusyan.alistgui.viewModel;

import atlantafx.base.theme.Theme;
import cn.octopusyan.alistgui.base.BaseTask;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.enums.ProxySetup;
import cn.octopusyan.alistgui.manager.ConfigManager;
import cn.octopusyan.alistgui.manager.http.HttpUtil;
import cn.octopusyan.alistgui.task.ProxyCheckTask;
import cn.octopusyan.alistgui.util.alert.AlertUtil;
import javafx.application.Platform;
import javafx.beans.property.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * 设置视图数据
 *
 * @author octopus_yan
 */
@Slf4j
public class SetupViewModel {
    private final BooleanProperty autoStart = new SimpleBooleanProperty(ConfigManager.autoStart());
    private final BooleanProperty silentStartup = new SimpleBooleanProperty(ConfigManager.silentStartup());
    private final ObjectProperty<Theme> theme = new SimpleObjectProperty<>(ConfigManager.theme());
    private final StringProperty proxyHost = new SimpleStringProperty(ConfigManager.proxyHost());
    private final StringProperty proxyPort = new SimpleStringProperty(ConfigManager.proxyPort());
    private final ObjectProperty<Locale> language = new SimpleObjectProperty<>(ConfigManager.language());
    private final ObjectProperty<ProxySetup> proxySetup = new SimpleObjectProperty<>(ConfigManager.proxySetup());
    private final StringProperty proxyTestUrl = new SimpleStringProperty(ConfigManager.proxyTestUrl());


    public SetupViewModel() {
        theme.bindBidirectional(Context.themeProperty());
        theme.addListener((_, _, newValue) -> ConfigManager.theme(newValue));
        autoStart.addListener((_, _, newValue) -> ConfigManager.autoStart(newValue));
        silentStartup.addListener((_, _, newValue) -> ConfigManager.silentStartup(newValue));
        proxySetup.addListener((_, _, newValue) -> ConfigManager.proxySetup(newValue));
        proxyTestUrl.addListener((_, _, newValue) -> ConfigManager.proxyTestUrl(newValue));
        proxyHost.addListener((_, _, newValue) -> ConfigManager.proxyHost(newValue));
        proxyPort.addListener((_, _, newValue) -> ConfigManager.proxyPort(newValue));
        language.addListener((_, _, newValue) -> Context.setLanguage(newValue));
    }

    public ObjectProperty<Theme> themeProperty() {
        return theme;
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

    public void proxyTest() {
        var checkUrl = AlertUtil.input("URL :", proxyTestUrl.getValue())
                .title(Context.getLanguageBinding("proxy.test.title").getValue())
                .header(Context.getLanguageBinding("proxy.test.header").getValue())
                .getInput();

        if (StringUtils.isEmpty(checkUrl)) return;

        proxyTestUrl.setValue(checkUrl);

        ConfigManager.checkProxy((success, msg) -> {
            if (!success) {
                final var tmp = Context.getLanguageBinding("proxy.test.result.failed").getValue();
                AlertUtil.error(STR."\{tmp}:\{msg}").show();
                return;
            }

            HttpUtil.getInstance().proxy(ConfigManager.proxySetup(), ConfigManager.getProxyInfo());
            getProxyCheckTask(checkUrl).execute();
        });
    }

    private static ProxyCheckTask getProxyCheckTask(String checkUrl) {
        var task = new ProxyCheckTask(checkUrl);
        final var progress = AlertUtil.progress();
        progress.title(Context.getLanguageBinding("proxy.test.title").get());
        task.onListen(new BaseTask.Listener() {

            @Override
            public void onRunning() {
                progress.onCancel(task::cancel).show();
            }

            @Override
            public void onSucceeded() {
                Platform.runLater(progress::close);
                AlertUtil.info(Context.getLanguageBinding("proxy.test.result.success").getValue()).show();
            }

            @Override
            public void onFailed(Throwable throwable) {
                Platform.runLater(progress::close);
                final var tmp = Context.getLanguageBinding("proxy.test.result.failed").getValue();
                String throwableMessage = throwable.getMessage();
                AlertUtil.error(tmp + (StringUtils.isEmpty(throwableMessage) ? "" : throwableMessage)).show();
            }
        });
        return task;
    }
}
