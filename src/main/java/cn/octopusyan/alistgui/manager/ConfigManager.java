package cn.octopusyan.alistgui.manager;

import atlantafx.base.theme.*;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.NumberUtil;
import cn.octopusyan.alistgui.Application;
import cn.octopusyan.alistgui.config.Constants;
import cn.octopusyan.alistgui.enums.ProxySetup;
import cn.octopusyan.alistgui.manager.http.HttpUtil;
import cn.octopusyan.alistgui.model.GuiConfig;
import cn.octopusyan.alistgui.model.ProxyInfo;
import cn.octopusyan.alistgui.model.UpgradeConfig;
import cn.octopusyan.alistgui.model.upgrade.AList;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
    public static ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    public static final Locale DEFAULT_LANGUAGE = Locale.SIMPLIFIED_CHINESE;
    private static GuiConfig guiConfig;
    private static UpgradeConfig upgradeConfig;

    public static final String DEFAULT_THEME = "Primer Light";
    public static List<Theme> THEME_LIST = Arrays.asList(
            new PrimerLight(), new PrimerDark(),
            new NordLight(), new NordDark(),
            new CupertinoLight(), new CupertinoDark(),
            new Dracula()
    );
    public static List<String> THEME_NAME_LIST = Arrays.asList(
            "Primer Light", "Primer Dark",
            "Nord Light", "Nord Dark",
            "Cupertino Light", "Cupertino Dark",
            "Dracula"
    );

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    public static void load() {
        guiConfig = loadConfig(Constants.GUI_CONFIG_PATH, GuiConfig.class);
        upgradeConfig = loadConfig(Constants.UPGRADE_PATH, UpgradeConfig.class);
    }

    private static <T> T loadConfig(String path, Class<T> clazz) {
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

// --------------------------------{ 主题 }------------------------------------------

    public static String themeName() {
        return guiConfig.getTheme();
    }

    public static Theme theme() {
        return THEME_LIST.get(THEME_NAME_LIST.indexOf(themeName()));
    }

    public static void themeName(String themeName) {
        int themeIndex = THEME_NAME_LIST.indexOf(themeName);
        if (themeIndex < 0) return;

        guiConfig.setTheme(themeName);
        Application.setUserAgentStylesheet(theme().getUserAgentStylesheet());
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
        return guiConfig.getProxyInfo();
    }

    private static void setProxyInfo(ProxyInfo info) {
        guiConfig.setProxyInfo(info);
    }

    public static String proxyHost() {
        if (getProxyInfo() == null) return null;
        return getProxyInfo().getHost();
    }

    public static void proxyHost(String host) {
        final Matcher matcher = PatternPool.IPV4.matcher(host);
        if (matcher.matches()) {
            if (getProxyInfo() == null)
                setProxyInfo(new ProxyInfo());
            getProxyInfo().setHost(host);
        }
    }

    public static String proxyPort() {
        if (getProxyInfo() == null) return null;
        return getProxyInfo().getPort();
    }

    public static int getProxyPort() {
        return Integer.parseInt(getProxyInfo().getPort());
    }

    public static void proxyPort(String port) {
        if (NumberUtil.isNumber(port)) {
            if (getProxyInfo() == null)
                setProxyInfo(new ProxyInfo());
            getProxyInfo().setPort(port);
        }
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

    public static boolean silentStartup() {
        return guiConfig.getSilentStartup();
    }

    public static void silentStartup(Boolean startup) {
        guiConfig.setSilentStartup(startup);
    }

    public static AList aList() {
        return upgradeConfig.getAList();
    }

    public static String aListVersion() {
        return upgradeConfig.getAList().getVersion();
    }

    public static void aListVersion(String version) {
        upgradeConfig.getAList().setVersion(version);
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
            objectMapper.writeValue(new File(Constants.UPGRADE_PATH), upgradeConfig);
        } catch (IOException e) {
            logger.error("save config error", e);
        }
    }
}
