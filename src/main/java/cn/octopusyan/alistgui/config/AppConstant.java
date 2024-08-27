package cn.octopusyan.alistgui.config;

import cn.octopusyan.alistgui.util.PropertiesUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * 应用信息
 *
 * @author octopus_yan@foxmail.com
 */
public class AppConstant {
    public static final String APP_TITLE = PropertiesUtils.getInstance().getProperty("app.title");
    public static final String APP_NAME = PropertiesUtils.getInstance().getProperty("app.name");
    public static final String APP_VERSION = PropertiesUtils.getInstance().getProperty("app.version");
    public static final String DATA_DIR_PATH = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Local" + File.separator + APP_NAME;
    public static final String TMP_DIR_PATH = FileUtils.getTempDirectoryPath() + APP_NAME;
    public static final String CUSTOM_CONFIG_PATH = DATA_DIR_PATH + File.separator + "config.properties";
    public static final String BAK_FILE_PATH = AppConstant.TMP_DIR_PATH + File.separator + "bak";
}
