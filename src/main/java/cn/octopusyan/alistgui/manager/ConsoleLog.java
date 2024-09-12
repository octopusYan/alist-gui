package cn.octopusyan.alistgui.manager;

import atlantafx.base.util.BBCodeParser;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 模拟控制台输出
 *
 * @author octopus_yan
 */
public class ConsoleLog {
    public static final String format = "yyyy/MM/dd hh:mm:ss";
    private volatile static ConsoleLog log;
    private final VBox textArea;
    private final static String CONSOLE_COLOR_PREFIX = "^\033\\[(\\d+)m(.*)\033\\[0m(.*)$";
    private final static String CONSOLE_COLOR_SUFFIX = "\033[0m";

    private ConsoleLog(ScrollPane logAreaSp, VBox textArea) {
        this.textArea = textArea;

        textArea.heightProperty().subscribe(() -> logAreaSp.vvalueProperty().setValue(1));
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
        printLog(tag, Level.INFO, message, param);
    }

    public static void warning(String tag, String message, Object... param) {
        printLog(tag, Level.WARN, message, param);
    }

    public static void error(String tag, String message, Object... param) {
        printLog(tag, Level.ERROR, message, param);
    }

    public static void msg(String message, Object... param) {
        if (message.contains("\033[")) {
            message = resetConsoleColor(message);
        }

        Node text = BBCodeParser.createFormattedText(STR."\{StrUtil.format(message, param)}");
        Platform.runLater(() -> log.textArea.getChildren().add(text));
    }

    public static void printLog(String tag, Level level, String message, Object... param) {

        String time = DateFormatUtils.format(new Date(), format);
        time = STR."[color=-color-accent-emphasis]\{time}[/color]";

        String levelStr = resetLevelColor(level);

        tag = StringUtils.isEmpty(tag) ? "" : STR."\{tag}: ";

        message = STR."[color=-color-fg-muted]\{StrUtil.format(message, param)}[/color]";

        Node text;
        String input = STR."\{time} \{levelStr} \{tag}\{message}";

        if (input.contains("\n")) {
            text = BBCodeParser.createLayout(input);
        } else {
            text = BBCodeParser.createFormattedText(input);
        }
        Platform.runLater(() -> log.textArea.getChildren().add(text));
    }

//============================{ 私有方法 }================================

    /**
     * 控制台输出颜色
     *
     * @param msg alist 输出消息
     * @return bbcode 颜色文本
     */
    private static String resetConsoleColor(String msg) {
        try {
            String colorCode = ReUtil.get(CONSOLE_COLOR_PREFIX, msg, 1);
            String color = StringUtils.lowerCase(Color.valueOf(Integer.parseInt(colorCode)).getColor());
            String colorMsg = ReUtil.get(CONSOLE_COLOR_PREFIX, msg, 2);
            msg = ReUtil.get(CONSOLE_COLOR_PREFIX, msg, 3);
            return color(color, colorMsg) + msg;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param level 级别
     * @return bbcode 颜色
     */
    private static String resetLevelColor(Level level) {
        return color(level.getColor(), level.getCode());
    }

    private static String color(String color, String msg) {
        String PREFIX = STR."\{StringUtils.isEmpty(color) ? "" : STR."[color=\{color}]"}";
        String SUFFIX = STR."\{StringUtils.isEmpty(color) ? "" : "[/color]"}";
        return STR."\{PREFIX}\{msg}\{SUFFIX}";
    }

//============================{ 枚举 }================================

    @Getter
    @RequiredArgsConstructor
    public enum Level {
        INFO("INFO", null),
        WARN("WARN", "color=-color-chart-2"),
        ERROR("ERROR", "color=-color-chart-3"),
        ;

        private final String code;
        private final String color;
    }

    @RequiredArgsConstructor
    @Getter
    enum Color {
        BLACK(30, "-color-fg-default"),
        RED(31, "-color-danger-fg"),
        GREEN(32, "-color-success-fg"),
        YELLOW(33, "-color-warning-emphasis"),
        BLUE(34, "-color-accent-fg"),
        PINKISH_RED(35, "-color-danger-4"),
        CYAN(36, "-color-accent-emphasis"),
        WHITE(37, "-color-bg-default");

        private final int code;
        private final String color;

        public static final Map<String, Color> NAME_CODE = Arrays.stream(Color.values())
                .collect(Collectors.toMap(Color::name, Function.identity()));

        public static final Map<Integer, Color> CODE_NAME = Arrays.stream(Color.values())
                .collect(Collectors.toMap(Color::getCode, Function.identity()));

        public static Color valueOf(int code) {
            return CODE_NAME.get(code);
        }
    }
}