package cn.octopusyan.alistgui.model;

import cn.octopusyan.alistgui.enums.ProxySetup;
import cn.octopusyan.alistgui.manager.ConfigManager;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GUI配置信息
 *
 * @author octopus_yan
 */
@Data
public class GuiConfig {
    private static final Logger log = LoggerFactory.getLogger(GuiConfig.class);

    private Boolean autoStart = false;
    private Boolean silentStartup = false;
    private ProxyInfo proxyInfo;
    private String proxySetup = ProxySetup.NO_PROXY.getName();
    private String language = ConfigManager.DEFAULT_LANGUAGE.toString();
    private String theme = ConfigManager.DEFAULT_THEME;
    private String proxyTestUrl = "http://";
}
