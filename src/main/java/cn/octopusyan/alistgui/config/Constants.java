package cn.octopusyan.alistgui.config;

import cn.octopusyan.alistgui.util.PropertiesUtils;

import java.io.File;
import java.nio.file.Paths;

/**
 * 应用信息
 *
 * @author octopus_yan@foxmail.com
 */
public class Constants {
    public static final String APP_TITLE = PropertiesUtils.getInstance().getProperty("app.title");
    public static final String APP_NAME = PropertiesUtils.getInstance().getProperty("app.name");
    public static final String APP_VERSION = PropertiesUtils.getInstance().getProperty("app.version");

    public static final String DATA_DIR_PATH = Paths.get(".").toFile().getAbsolutePath();
    public static final String BIN_DIR_PATH = STR."\{DATA_DIR_PATH}\{File.separator}bin";
    public static final String TMP_DIR_PATH = System.getProperty("java.io.tmpdir") + APP_NAME;

    public static final String ALIST_FILE = STR."\{BIN_DIR_PATH}\{File.separator}alist.exe";
    public static final String CONFIG_DIR_PATH = STR."\{DATA_DIR_PATH}\{File.separator}config";
    public static final String GUI_CONFIG_PATH = STR."\{CONFIG_DIR_PATH}\{File.separator}gui.yaml";
    public static final String BAK_FILE_PATH = STR."\{Constants.TMP_DIR_PATH}\{File.separator}bak";

    public static final String REG_AUTO_RUN = "Software\\Microsoft\\Windows\\CurrentVersion\\Run";
    public static final String APP_EXE = STR."\{DATA_DIR_PATH}\{File.separator}\{APP_NAME}.exe";
}
