package cn.octopusyan.alistgui.manager;

import atlantafx.base.theme.*;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.octopusyan.alistgui.Application;
import cn.octopusyan.alistgui.config.Constants;
import cn.octopusyan.alistgui.enums.ProxySetup;
import cn.octopusyan.alistgui.manager.http.HttpUtil;
import cn.octopusyan.alistgui.manager.thread.ThreadPoolManager;
import cn.octopusyan.alistgui.model.GuiConfig;
import cn.octopusyan.alistgui.model.ProxyInfo;
import cn.octopusyan.alistgui.model.UpgradeConfig;
import cn.octopusyan.alistgui.model.upgrade.AList;
import cn.octopusyan.alistgui.model.upgrade.Gui;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * 客户端设置
 *
 * @author octopus_yan@foxmail.com
 */
public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    public static ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    public static final Locale DEFAULT_LANGUAGE = Locale.SIMPLIFIED_CHINESE;
    public static final String DEFAULT_THEME = new PrimerLight().getName();
    public static List<Theme> THEME_LIST = List.of(
            new PrimerLight(), new PrimerDark(),
            new NordLight(), new NordDark(),
            new CupertinoLight(), new CupertinoDark(),
            new Dracula()
    );
    public static Map<String, Theme> THEME_MAP = THEME_LIST.stream()
            .collect(Collectors.toMap(Theme::getName, Function.identity()));

    private static GuiConfig guiConfig;

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    public static void load() {
        guiConfig = loadConfig(Constants.GUI_CONFIG_PATH, GuiConfig.class);
    }

    public static <T> T loadConfig(String path, Class<T> clazz) {
        File src = new File(path);
        try {
            if (!src.exists()) {
                checkFile(src, clazz);
            }
            return objectMapper.readValue(src, clazz);
        } catch (Exception e) {
            logger.error(String.format("load %s error", clazz.getSimpleName()), e);
        }
        return null;
    }

    private static <T> void checkFile(File src, Class<T> clazz) throws Exception {
        File parent = FileUtil.getParent(src, 1);
        if (!parent.exists()) {
            boolean wasSuccessful = parent.mkdirs();
            objectMapper.writeValue(src, clazz.getDeclaredConstructor().newInstance());
            if (!wasSuccessful)
                logger.error("{} 创建失败", src.getAbsolutePath());
        }
    }

    public static void save() {
        try {
            objectMapper.writeValue(new File(Constants.GUI_CONFIG_PATH), guiConfig);
        } catch (IOException e) {
            logger.error("save config error", e);
        }
    }

// --------------------------------{ 主题 }------------------------------------------

    public static String themeName() {
        return guiConfig.getTheme();
    }

    public static Theme theme() {
        return THEME_MAP.get(themeName());
    }

    public static void theme(Theme theme) {
        Application.setUserAgentStylesheet(theme.getUserAgentStylesheet());
        guiConfig.setTheme(theme.getName());
    }

// --------------------------------{ 网络代理 }------------------------------------------

    public static ProxySetup proxySetup() {
        return ProxySetup.valueOf(StringUtils.upperCase(guiConfig.getProxySetup()));
    }

    public static void proxyTestUrl(String url) {
        guiConfig.setProxyTestUrl(url);
    }

    public static String proxyTestUrl() {
        return guiConfig.getProxyTestUrl();
    }

    public static void proxySetup(ProxySetup setup) {
        guiConfig.setProxySetup(setup.getName());

        switch (setup) {
            case NO_PROXY -> HttpUtil.getInstance().clearProxy();
            case SYSTEM, MANUAL -> {
                if (ProxySetup.MANUAL.equals(setup) && !hasProxy())
                    return;
                HttpUtil.getInstance().proxy(setup, ConfigManager.getProxyInfo());
            }
        }
    }

    public static boolean hasProxy() {
        if (guiConfig == null)
            return false;
        ProxyInfo proxyInfo = getProxyInfo();
        return proxyInfo != null
                && StringUtils.isNoneEmpty(proxyInfo.getHost())
                && StringUtils.isNoneEmpty(proxyInfo.getPort())
                && Integer.parseInt(proxyInfo.getPort()) > 0;
    }

    public static ProxyInfo getProxyInfo() {
        ProxyInfo proxyInfo = guiConfig.getProxyInfo();

        if (proxyInfo == null)
            setProxyInfo(new ProxyInfo());

        return guiConfig.getProxyInfo();
    }

    private static void setProxyInfo(ProxyInfo info) {
        guiConfig.setProxyInfo(info);
    }

    public static String proxyHost() {
        return getProxyInfo().getHost();
    }

    public static void proxyHost(String host) {
        final Matcher matcher = PatternPool.IPV4.matcher(host);
        if (!matcher.matches()) return;

        getProxyInfo().setHost(host);
    }

    public static String proxyPort() {
        return getProxyInfo().getPort();
    }

    public static int getProxyPort() {
        return Integer.parseInt(proxyPort());
    }

    public static void proxyPort(String port) {
        if (!NumberUtil.isNumber(port)) return;

        getProxyInfo().setPort(port);
    }

    public static void checkProxy(BiConsumer<Boolean, String> consumer) {
        if (!hasProxy()) return;

        ThreadPoolManager.getInstance().execute(() -> {
            try {
                InetSocketAddress address = NetUtil.createAddress(proxyHost(), getProxyPort());
                if (NetUtil.isOpen(address, 5000)) {
                    Platform.runLater(() -> consumer.accept(true, "success"));
                } else {
                    Platform.runLater(() -> consumer.accept(false, "connection timed out"));
                }
            } catch (Exception e) {
                logger.error(STR."host=\{proxyHost()},port=\{proxyPort()}", e);
                Platform.runLater(() -> consumer.accept(false, e.getMessage()));
            }
        });
    }

// --------------------------------{ 语言 }------------------------------------------

    public static Locale language() {
        String language = guiConfig.getLanguage();
        return LocaleUtils.toLocale(Optional.ofNullable(language).orElse(DEFAULT_LANGUAGE.toString()));
    }

    public static void language(Locale locale) {
        guiConfig.setLanguage(locale.toString());
    }

// --------------------------------{ 开机自启 }------------------------------------------

    public static boolean autoStart() {
        return guiConfig.getAutoStart();
    }

    public static void autoStart(Boolean autoStart) {
        guiConfig.setAutoStart(autoStart);
    }

// --------------------------------{ 静默启动 }------------------------------------------

    public static boolean silentStartup() {
        return guiConfig.getSilentStartup();
    }

    public static void silentStartup(Boolean startup) {
        guiConfig.setSilentStartup(startup);
    }

// --------------------------------{ 最小化到托盘 }------------------------------------------

    public static boolean closeToTray() {
        return guiConfig.getCloseToTray();
    }

    public static void closeToTray(boolean check) {
        guiConfig.setCloseToTray(check);
    }

// --------------------------------{ 版本检查 }------------------------------------------

    public static UpgradeConfig upgradeConfig() {
        return guiConfig.getUpgradeConfig();
    }

    public static AList aList() {
        return upgradeConfig().getAList();
    }

    public static String aListVersion() {
        return aList().getVersion();
    }

    public static StringProperty aListVersionProperty() {
        return aList().versionProperty();
    }

    public static void aListVersion(String version) {
        aListVersionProperty().set(version);
    }

    public static Gui gui() {
        return upgradeConfig().getGui();
    }

    public static String guiVersion() {
        return gui().getVersion();
    }

    public static void guiVersion(String version) {
        gui().setVersion(version);
    }
}
