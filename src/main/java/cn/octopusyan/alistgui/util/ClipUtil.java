package cn.octopusyan.alistgui.util;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * 剪切板工具
 *
 * @author octopus_yan@foxmail.com
 */
public class ClipUtil {
    //获取系统剪切板
    private static final Clipboard clipboard = Clipboard.getSystemClipboard();

    public static void setClip(String data) {
        clipboard.clear();
        // 设置剪切板内容
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(data);
        clipboard.setContent(clipboardContent);

    }

    public static String getString() {
        // javafx 从剪切板获取文本
        return clipboard.getString();
    }
}
