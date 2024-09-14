package cn.octopusyan.alistgui.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.octopusyan.alistgui.config.Constants;
import cn.octopusyan.alistgui.manager.ConsoleLog;
import cn.octopusyan.alistgui.model.upgrade.AList;
import cn.octopusyan.alistgui.model.upgrade.UpgradeApp;
import cn.octopusyan.alistgui.task.DownloadTask;
import cn.octopusyan.alistgui.task.listener.TaskListener;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipFile;

/**
 * 下载工具
 *
 * @author octopus_yan
 */
@Slf4j
public class DownloadUtil {


    /**
     * 下载文件
     *
     * @param app     应用
     * @param version 下载版本
     */
    public static DownloadTask startDownload(UpgradeApp app, String version, Runnable runnable) {
        var task = new DownloadTask(app.getDownloadUrl(version));
        task.onListen(new TaskListener.DownloadListener(task) {

            @Override
            public void onRunning() {
                // 不展示进度条
            }

            @Override
            public void onSucceed() {
                String msg = STR."download \{app.getRepo()} success";
                log.info(msg);
                ConsoleLog.info(msg);

                runnable.run();
            }
        });
        return task;
    }

    public static void unzip(UpgradeApp app) {
        String parentPath = app instanceof AList ? Constants.BIN_DIR_PATH : Constants.DATA_DIR_PATH;

        File file = new File(parentPath + File.separator + app.getReleaseFile());
        ZipFile zipFile = ZipUtil.toZipFile(file, CharsetUtil.defaultCharset());
        ZipUtil.read(zipFile, zipEntry -> {
            String path = zipEntry.getName();
            if (FileUtil.isWindows()) {
                // Win系统下
                path = StrUtil.replace(path, "*", "_");
            }

            final File outItemFile = FileUtil.file(parentPath, path);
            if (zipEntry.isDirectory()) {
                // 目录
                //noinspection ResultOfMethodCallIgnored
                outItemFile.mkdirs();
            } else {
                InputStream in = ZipUtil.getStream(zipFile, zipEntry);
                // 文件
                FileUtil.writeFromStream(in, outItemFile, false);

                log.info(STR."unzip ==> \{outItemFile.getAbsoluteFile()}");
            }
        });

        // 解压完成后删除
        FileUtil.del(file);
    }
}
