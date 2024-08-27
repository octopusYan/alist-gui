package cn.octopusyan.alistgui.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.CanReadFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件工具类
 *
 * @author octopus_yan@foxmail.com
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static File[] ls(String path) {
        File dir = new File(path);
        if (!dir.exists())
            throw new RuntimeException(path + "不存在!");

        if (!dir.isDirectory())
            throw new RuntimeException(path + "不是一个文件夹!");

        return dir.listFiles();
    }

    public static void copyFilesFromDir(String path, String dest) throws IOException {
        if (StringUtils.isBlank(path) || StringUtils.isBlank(dest)) {
            logger.error("path is blank !");
            return;
        }
        File dir = new File(path);
        if (!dir.exists()) {
            logger.error("[" + path + "] 不存在！");
            return;
        }
        if (!dir.isDirectory()) {
            logger.error("[" + path + "] 不是一个文件夹！");
        }

        File[] files = dir.listFiles();
        if (files == null) return;

        File directory = new File(dest);
        if (directory.exists() && !directory.isDirectory()) {
            logger.error("[" + dest + "] 不是一个文件夹！");
        }

        FileUtils.forceMkdir(directory);

        for (File file : files) {
            copyFile(file, new File(dest + File.separator + file.getName()));
        }
    }

    public static void copyFile(File in, File out) throws IOException {
        copyFile(Files.newInputStream(in.toPath()), out);
    }

    public static void copyFile(InputStream input, File out) throws IOException {
        OutputStream output = null;
        try {
            output = Files.newOutputStream(out.toPath());
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            if (output != null) input.close();
            if (output != null) output.close();
        }
    }

    /**
     * 获取文件主名称
     *
     * @param file 文件对象
     * @return 文件名称
     */
    public static String mainName(File file) {
        //忽略判断
        String fileName = file.getName();
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static List<String> listFileNames(String path) {
        Collection<File> files = FileUtils.listFiles(new File(path), CanReadFileFilter.CAN_READ, null);
        return files.stream().map(File::getName).collect(Collectors.toList());
    }

    /**
     * 返回被查找到的文件的绝对路径（匹配到一个就返回）
     *
     * @param root     根目录文件
     * @param fileName 要找的文件名
     * @return 绝对路径
     */
    private static String findFiles(File root, String fileName) {
        //定义一个返回值
        String path = null;
        //如果传进来的是目录，并且存在
        if (root.exists() && root.isDirectory()) {
            //遍历文件夹中的各个文件
            File[] files = root.listFiles();
            if (files != null) {
                for (File file : files) {
                    //如果path的值没有变化
                    if (path == null) {
                        if (file.isFile() && file.getName().contains(fileName)) {
                            path = file.getAbsolutePath();
                        } else {
                            path = findFiles(file, fileName);
                        }
                    } else {
                        break;//跳出循环，增加性能
                    }
                }
            }
        }
        return path;
    }
}
