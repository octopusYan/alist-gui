package cn.octopusyan.alistgui.manager;

import atlantafx.base.util.BBCodeParser;
import cn.hutool.core.util.StrUtil;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * 模拟控制台输出
 *
 * @author octopus_yan
 */
public class ConsoleLog {
    public static final String format = "yyyy/MM/dd hh:mm:ss";
    private volatile static ConsoleLog log;
    private final ScrollPane logAreaSp;
    private final VBox textArea;

    private ConsoleLog(ScrollPane logAreaSp, VBox textArea) {
        this.textArea = textArea;
        this.logAreaSp = logAreaSp;

        textArea.maxWidthProperty().bind(logAreaSp.prefWidthProperty());

        textArea.heightProperty().subscribe(() -> {
            logAreaSp.vvalueProperty().setValue(1);
        });
    }

    public static ConsoleLog getInstance() {
        if (log == null) {
            throw new RuntimeException("are you ready ?");
        }
        return log;
    }

    public static void init(ScrollPane logAreaSp, VBox textArea) {
        synchronized (ConsoleLog.class) {
            log = new ConsoleLog(logAreaSp, textArea);
        }
    }

    public static boolean isInit() {
        return log != null;
    }

    public static void info(String message, Object... param) {
        info("", message, param);
    }

    public static void warning(String message, Object... param) {
        warning("", message, param);
    }

    public static void error(String message, Object... param) {
        error("", message, param);
    }

    public static void info(String tag, String message, Object... param) {
        printLog(tag, "INFO", message, param);
    }

    public static void warning(String tag, String message, Object... param) {
        printLog(tag, "[color=-color-chart-2]WARN[/color]", message, param);
    }

    public static void error(String tag, String message, Object... param) {
        printLog(tag, "[color=-color-chart-3]ERROR[/color]", message, param);
    }

    public static void msg(String message, Object... param) {
        Node text = BBCodeParser.createFormattedText(STR."\{StrUtil.format(message, param)}\n\n");
        Platform.runLater(() -> log.textArea.getChildren().add(text));
    }

    public static void printLog(String tag, String level, String message, Object... param) {

        String time = DateFormatUtils.format(new Date(), format);
        time = STR."[color=-color-accent-emphasis]\{time}[/color]";

        String levelStr = StringUtils.substringBetween(level, "]", "[");
        if (levelStr == null) levelStr = level;
        level = StringUtils.replace(level, levelStr, StringUtils.center(levelStr, 5));

        tag = StringUtils.isEmpty(tag) ? "" : STR."\{tag}: ";

        String msg = STR."[color=-color-fg-muted]\{StrUtil.format(message, param)}[/color]";

        Node text;
        if (msg.contains("\n")) {
            text = BBCodeParser.createLayout(STR."\{time} \{level} \{tag}\{msg}");
        } else {
            text = BBCodeParser.createFormattedText(STR."\{time} \{level} \{tag}\{msg}");
        }
        Platform.runLater(() -> log.textArea.getChildren().add(text));
    }
}
