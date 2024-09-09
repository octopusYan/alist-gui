package cn.octopusyan.alistgui.viewModel;

import cn.hutool.core.net.NetUtil;
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

import java.net.InetSocketAddress;
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
    private final StringProperty proxyHost = new SimpleStringProperty(ConfigManager.proxyHost());
    private final StringProperty proxyPort = new SimpleStringProperty(ConfigManager.proxyPort());
    private final ObjectProperty<Locale> language = new SimpleObjectProperty<>(ConfigManager.language());
    private final ObjectProperty<ProxySetup> proxySetup = new SimpleObjectProperty<>(ConfigManager.proxySetup());
    private final StringProperty proxyTestUrl = new SimpleStringProperty(ConfigManager.proxyTestUrl());


    public SetupViewModel() {
        autoStart.addListener((_, _, newValue) -> ConfigManager.autoStart(newValue));
        silentStartup.addListener((_, _, newValue) -> ConfigManager.silentStartup(newValue));
        proxySetup.addListener((_, _, newValue) -> ConfigManager.proxySetup(newValue));
        proxyTestUrl.addListener((_, _, newValue) -> ConfigManager.proxyTestUrl(newValue));
        proxyHost.addListener((_, _, newValue) -> {
            ConfigManager.proxyHost(newValue);
            checkProxy();
        });
        proxyPort.addListener((_, _, newValue) -> {
            ConfigManager.proxyPort(newValue);
            checkProxy();
        });
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

    public void proxyTest() {
        var checkUrl = AlertUtil.input("URL :", proxyTestUrl.getValue())
                .title(Context.getLanguageBinding("proxy.test.title").getValue())
                .header(Context.getLanguageBinding("proxy.test.header").getValue())
                .getInput();

        if (StringUtils.isEmpty(checkUrl)) return;

        proxyTestUrl.setValue(checkUrl);

        getProxyCheckTask(checkUrl).execute();
    }

    private void checkProxy() {
        try {
            InetSocketAddress address = NetUtil.createAddress(proxyHost.get(), Integer.parseInt(proxyPort.getValue()));
            if (NetUtil.isOpen(address, 2000)) {
                HttpUtil.getInstance().proxy(proxySetup.get(), ConfigManager.getProxyInfo());
            }
        } catch (Exception e) {
            log.debug(STR."host=\{proxyHost.get()},port=\{proxyPort.get()}");
        }
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
                AlertUtil.error(tmp + throwable.getMessage()).show();
            }
        });
        return task;
    }
}
