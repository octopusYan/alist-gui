package cn.octopusyan.alistgui.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件读取工具
 *
 * @author liubin5620
 * @see <a href="https://blog.csdn.net/liubin5620/article/details/104618950">配置文件信息读取工具类【PropertiesUtils】</a>
 */
public class PropertiesUtils {
    /**
     * 主配置文件
     */
    private final Properties properties;
    /**
     * 启用配置文件
     */
    private final Properties propertiesCustom;

    private static PropertiesUtils propertiesUtils = new PropertiesUtils();

    public static final Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

    /**
     * 私有构造，禁止直接创建
     */
    private PropertiesUtils() {
        // 读取配置启用的配置文件名
        properties = new Properties();
        propertiesCustom = new Properties();
        InputStream in = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties");
        try {
            properties.load(in);
            // 加载启用的配置
            String property = properties.getProperty("profiles.active");
            if (!StringUtils.isBlank(property)) {
                InputStream cin = PropertiesUtils.class.getClassLoader().getResourceAsStream("application-" + property + ".properties");
                propertiesCustom.load(cin);
            }
        } catch (IOException e) {
            logger.error("读取配置文件失败", e);
        }
    }

    /**
     * 获取单例
     *
     * @return PropertiesUtils
     */
    public static PropertiesUtils getInstance() {
        if (propertiesUtils == null) {
            propertiesUtils = new PropertiesUtils();
        }
        return propertiesUtils;
    }

    /**
     * 根据属性名读取值
     * 先去主配置查询，如果查询不到，就去启用配置查询
     *
     * @param name 名称
     */
    public String getProperty(String name) {
        String val = properties.getProperty(name);
        if (StringUtils.isBlank(val)) {
            val = propertiesCustom.getProperty(name);
        }
        return val;
    }
}
