package cn.octopusyan.alistgui.config;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * 客户端设置
 *
 * @author octopus_yan@foxmail.com
 */
public class CustomConfig {
    private static final Logger logger = LoggerFactory.getLogger(CustomConfig.class);
    private static final Properties properties = new Properties();
    public static final String PROXY_HOST_KEY = "proxy.host";
    public static final String PROXY_PORT_KEY = "proxy.port";

    public static void init() {
        FileReader reader = null;
        try {
            File file = new File(AppConstant.CUSTOM_CONFIG_PATH);
            if (!file.exists()) {
                // 创建配置文件
                if (!file.getParentFile().exists()) {
                    FileUtils.createParentDirectories(file);
                }
                boolean newFile = file.createNewFile();
                // 保存配置
                store();
            } else {
                reader = new FileReader(file);
                properties.load(reader);
            }
        } catch (Exception e) {
            logger.error("读取配置文件失败", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                logger.error("关闭配置文件流", e);
            }
        }
    }

    /**
     * 是否配置代理
     */
    public static boolean hasProxy() {
        String host = proxyHost();
        Integer port = proxyPort();

        return StringUtils.isNoneBlank(host) && Objects.nonNull(port);
    }

    /**
     * 代理地址
     */
    public static String proxyHost() {
        return properties.getProperty(PROXY_HOST_KEY);
    }

    /**
     * 代理地址
     */
    public static void proxyHost(String host) {
        properties.setProperty(PROXY_HOST_KEY, host);
    }

    /**
     * 代理端口
     */
    public static Integer proxyPort() {
        try {
            return Integer.parseInt(properties.getProperty(PROXY_PORT_KEY));
        } catch (Exception ignored) {
        }
        return 10809;
    }

    /**
     * 代理端口
     */
    public static void proxyPort(int port) {
        properties.setProperty(PROXY_PORT_KEY, String.valueOf(port));
    }


    /**
     * 保存配置
     */
    public static void store() {
        // 生成配置文件
        try {
            properties.store(new PrintStream(AppConstant.CUSTOM_CONFIG_PATH), String.valueOf(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("保存客户端配置失败", e);
        }
    }
}
