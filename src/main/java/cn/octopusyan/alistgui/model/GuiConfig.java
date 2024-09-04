package cn.octopusyan.alistgui.model;

import cn.hutool.core.io.FileUtil;
import cn.octopusyan.alistgui.config.AppConstant;
import cn.octopusyan.alistgui.config.ConfigManager;
import cn.octopusyan.alistgui.enums.ProxySetup;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * GUI配置信息
 * 通过Jackson 写入yaml
 *
 * @author octopus_yan
 */
@Data
public class GuiConfig {
    private static final Logger log = LoggerFactory.getLogger(GuiConfig.class);
    public static GuiConfig INSTANCE;
    public static ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    private Boolean autoStart = false;
    private Boolean silentStartup = false;
    private ProxyInfo proxyInfo = new ProxyInfo();
    private String proxySetup = ProxySetup.NO_PROXY.getName();
    private String language = ConfigManager.DEFAULT_LANGUAGE.toString();
    @JsonProperty("aListVersion")
    private String aListVersion = "unknown";

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    private GuiConfig() {

    }

    public static GuiConfig getInstance() throws IOException {
        File src = new File(AppConstant.GUI_CONFIG_PATH);
        if (INSTANCE == null) {
            if (!src.exists()) {
                checkFile(src);
            }
            INSTANCE = objectMapper.readValue(src, GuiConfig.class);
        }
        return INSTANCE;
    }

    private static void checkFile(File src) throws IOException {
        File parent = FileUtil.getParent(src, 1);
        if (!parent.exists()) {
            boolean wasSuccessful = parent.mkdirs();
            objectMapper.writeValue(src, new GuiConfig());
            if (!wasSuccessful)
                log.error("{} 创建失败", src.getAbsolutePath());
        }
    }

    public void save() throws IOException {
        objectMapper.writeValue(new File(AppConstant.GUI_CONFIG_PATH), INSTANCE);
    }
}
