package cn.octopusyan.alistgui.config;

import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.NumberUtil;
import cn.octopusyan.alistgui.enums.ProxySetup;
import cn.octopusyan.alistgui.manager.http.HttpUtil;
import cn.octopusyan.alistgui.model.GuiConfig;
import cn.octopusyan.alistgui.model.ProxyInfo;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;

/**
 * 客户端设置
 *
 * @author octopus_yan@foxmail.com
 */
public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    public static final Locale DEFAULT_LANGUAGE = Locale.CHINESE;
    private static GuiConfig guiConfig;

    public static void load() {
        try {
            guiConfig = GuiConfig.getInstance();
        } catch (IOException e) {
            logger.error("load config error", e);
        }
    }

    public static boolean hasProxy() {
        if (guiConfig == null)
            return false;
        ProxyInfo proxyInfo = guiConfig.getProxyInfo();
        return proxyInfo != null
                && StringUtils.isNoneEmpty(proxyInfo.getHost())
                && StringUtils.isNoneEmpty(proxyInfo.getPort())
                && Integer.parseInt(proxyInfo.getPort()) > 0;
    }

    public static ProxyInfo getProxyInfo() {
        return guiConfig.getProxyInfo();
    }

    public static String proxyHost() {
        return guiConfig.getProxyInfo().getHost();
    }

    public static void proxyHost(String host) {
        final Matcher matcher = PatternPool.IPV4.matcher(host);
        if (matcher.matches()) {
            guiConfig.getProxyInfo().setHost(host);
        }
    }

    public static String proxyPort() {
        return guiConfig.getProxyInfo().getPort();
    }

    public static int getProxyPort() {
        return Integer.parseInt(guiConfig.getProxyInfo().getPort());
    }

    public static void proxyPort(String port) {
        if (NumberUtil.isNumber(port))
            guiConfig.getProxyInfo().setPort(port);
    }

    public static Locale language() {
        String language = guiConfig.getLanguage();
        return LocaleUtils.toLocale(Optional.ofNullable(language).orElse(DEFAULT_LANGUAGE.toString()));
    }

    public static void language(Locale locale) {
        guiConfig.setLanguage(locale.toString());
    }

    public static boolean autoStart() {
        return guiConfig.getAutoStart();
    }

    public static void autoStart(Boolean autoStart) {
        guiConfig.setAutoStart(autoStart);
    }

    public static ProxySetup proxySetup() {
        return ProxySetup.valueOf(StringUtils.upperCase(guiConfig.getProxySetup()));
    }

    public static void proxySetup(ProxySetup setup) {
        guiConfig.setProxySetup(setup.getName());

        if (!ProxySetup.NO_PROXY.equals(setup) && hasProxy()) {
            HttpUtil.getInstance().proxy(setup, ConfigManager.getProxyInfo());
        }
    }

    public static boolean silentStartup() {
        return guiConfig.getSilentStartup();
    }

    public static void silentStartup(Boolean startup) {
        guiConfig.setSilentStartup(startup);
    }

    public static String aListVersion() {
        return guiConfig.getAListVersion();
    }

    public static void aListVersion(String version) {
        guiConfig.setAListVersion(version);
    }

    public static void save() {
        try {
            guiConfig.save();
        } catch (IOException e) {
            logger.error("save config error", e);
        }
    }
}
