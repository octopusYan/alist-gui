package cn.octopusyan.alistgui.config;

import cn.octopusyan.alistgui.util.PropertiesUtils;

import java.io.File;
import java.nio.file.Paths;

/**
 * 应用信息
 *
 * @author octopus_yan@foxmail.com
 */
public class AppConstant {
    public static final String APP_TITLE = PropertiesUtils.getInstance().getProperty("app.title");
    public static final String APP_NAME = PropertiesUtils.getInstance().getProperty("app.name");
    public static final String APP_VERSION = PropertiesUtils.getInstance().getProperty("app.version");
    public static final String DATA_DIR_PATH = Paths.get(".").toFile().getAbsolutePath();
    public static final String TMP_DIR_PATH = System.getProperty("java.io.tmpdir") + APP_NAME;
    public static final String CONFIG_DIR_PATH = DATA_DIR_PATH + File.separator + "config";
    public static final String GUI_CONFIG_PATH = CONFIG_DIR_PATH + File.separator + "gui.yaml";
    public static final String BAK_FILE_PATH = AppConstant.TMP_DIR_PATH + File.separator + "bak";
}
