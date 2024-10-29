package cn.octopusyan.upgrade.view;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.octopusyan.upgrade.alert.AlertUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;

import java.io.*;
import java.nio.file.Paths;
import java.util.zip.ZipFile;

public class HelloController {
    public static final String APP_NAME = "alist-gui";
    public static final String PARENT_PATH = Paths.get("").toFile().getAbsolutePath();

    @FXML
    public void onUpdate() {
        // 更新检查
        File file = new File(PARENT_PATH + File.separator + APP_NAME + "-windows-nojre.zip");
        if (!file.exists()) {
            AlertUtil.error("The upgrade file does not exist!").show();
            return;
        }

        // 解压
        unzip();
        // 启动GUI
        startGui();
        // 退出
        onExit();
    }

    @FXML
    public void onExit() {
        Platform.exit();
    }

    private void startGui() {
        Runtime runtime = Runtime.getRuntime();  //获取Runtime实例
        //执行命令
        String[] command = {APP_NAME + ".exe"};
        try {
            Process process = runtime.exec(command);
        } catch (IOException ignored) {

        }
    }


    private void unzip() {


        File file = new File(PARENT_PATH + File.separator + APP_NAME + "-windows-nojre.zip");
        ZipFile zipFile = ZipUtil.toZipFile(file, CharsetUtil.defaultCharset());
        ZipUtil.read(zipFile, zipEntry -> {
            String path = zipEntry.getName();
            if (FileUtil.isWindows()) {
                // Win系统下
                path = StrUtil.replace(path, "*", "_");
            }

            // 打包后文件都在alist-gui文件夹下，解压时去掉
            path = path.replaceFirst(APP_NAME, "");

            final File outItemFile = FileUtil.file(PARENT_PATH, path);
            if (zipEntry.isDirectory()) {
                // 目录
                //noinspection ResultOfMethodCallIgnored
                outItemFile.mkdirs();
            } else {
                InputStream in = ZipUtil.getStream(zipFile, zipEntry);
                // 文件
                FileUtil.writeFromStream(in, outItemFile, false);
            }
        });

        // 解压完成后删除
        FileUtil.del(file);
    }
}