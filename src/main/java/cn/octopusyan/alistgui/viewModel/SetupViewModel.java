package cn.octopusyan.alistgui.viewModel;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.net.url.UrlBuilder;
import cn.octopusyan.alistgui.config.ConfigManager;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.enums.ProxySetup;
import javafx.beans.property.*;
import org.apache.commons.lang3.StringUtils;

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
    private final BooleanProperty proxyVerify = new SimpleBooleanProperty(false);
    private final ObjectProperty<Locale> language = new SimpleObjectProperty<>(ConfigManager.language());
    private final ObjectProperty<ProxySetup> proxySetup = new SimpleObjectProperty<>(ConfigManager.proxySetup());
    private final SimpleStringProperty aListVersion = new SimpleStringProperty(ConfigManager.aListVersion());

    public SetupViewModel() {
        aListVersion.addListener((observable, oldValue, newValue) -> ConfigManager.aListVersion(newValue));
        autoStart.addListener((observable, oldValue, newValue) -> ConfigManager.autoStart(newValue));
        silentStartup.addListener((observable, oldValue, newValue) -> ConfigManager.silentStartup(newValue));
        proxySetup.addListener((observable, oldValue, newValue) -> ConfigManager.proxySetup(newValue));
        proxyHost.addListener((observable, oldValue, newValue) -> ConfigManager.proxyHost(newValue));
        proxyPort.addListener((observable, oldValue, newValue) -> ConfigManager.proxyPort(newValue));
        language.addListener((observable, oldValue, newValue) -> Context.setLanguage(newValue));
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

    /**
     * 验证代理地址
     *
     * @param address 新的代理地址
     * @return UrlBuilder, 验证失败时为null
     */
    public UrlBuilder validateAddress(String address) {
        if (StringUtils.isEmpty(address))
            return null;

        // 验证 URL 的可用性
        UrlBuilder ub = UrlBuilder.of(address);
        boolean isUrlValid = Validator.isUrl(address) && ub.getPort() > 0;

        // 设置 proxyVerify 和 proxyVerifyMsg
        proxyVerify.setValue(isUrlValid);

        if (isUrlValid)
            return ub;
        else return null;
    }
}
