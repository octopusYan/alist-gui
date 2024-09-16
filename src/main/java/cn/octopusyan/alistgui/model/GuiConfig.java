package cn.octopusyan.alistgui.model;

import cn.octopusyan.alistgui.enums.ProxySetup;
import cn.octopusyan.alistgui.manager.ConfigManager;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Boolean closeToTray = true;
    @JsonProperty("proxy")
    private ProxyInfo proxyInfo;
    @JsonProperty("proxy.testUrl")
    private String proxyTestUrl = "http://";
    private String proxySetup = ProxySetup.NO_PROXY.getName();
    private String language = ConfigManager.DEFAULT_LANGUAGE.toString();
    private String theme = ConfigManager.DEFAULT_THEME;
    @JsonProperty("upgrade")
    private UpgradeConfig upgradeConfig = new UpgradeConfig();
}
