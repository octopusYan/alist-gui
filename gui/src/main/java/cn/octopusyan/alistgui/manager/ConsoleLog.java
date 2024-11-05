package cn.octopusyan.alistgui.manager;

import atlantafx.base.controls.Popover;
import atlantafx.base.util.BBCodeParser;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.octopusyan.alistgui.config.Context;
import javafx.application.Platform;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private final static String CONSOLE_COLOR_PREFIX = "\033[";
    private final static String CONSOLE_MSG_REX = "^\033\\[(\\d+)m(.*)\033\\[0m(.*)$";
    private final static String URL_IP_REX = "^((ht|f)tps?:\\/\\/)?[\\w-+&@#/%?=~_|!:,.;]*[\\w-+&@#/%=~_|]+(:\\d{1,5})?\\/?$";

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
        if (StringUtils.isEmpty(message) || !isInit()) return;
        message = message.strip();
        message = StrUtil.format(message, param);
        message = setPwdText(message);
        message = resetConsoleColor(message);

        print(message);
    }

    public static void printLog(String tag, Level level, String message, Object... param) {
        if (!isInit()) return;

        // 时间
        String time = DateFormatUtils.format(new Date(), format);
        time = STR."[color=-color-accent-emphasis]\{time}[/color]";
        // 级别
        String levelStr = resetLevelColor(level);
        // 标签
        tag = StringUtils.isEmpty(tag) ? "" : STR."\{tag}: ";
        // 消息
        message = STR."[color=-color-fg-muted]\{StrUtil.format(message, param)}[/color]";

        // 拼接后输出
        String input = STR."\{time} \{levelStr} - \{tag}\{message}";

        print(input);
    }

    private static void print(String message) {

        // 标记链接
        String regex = STR.".*(\{AListManager.scheme()}|\{URL_IP_REX}).*";
        if (ReUtil.isMatch(regex, message)) {
            String text = ReUtil.get(regex, message, 1);
            String url = text.startsWith("http") ? text : STR."http://\{text}";
            url = url.replace("0.0.0.0", "127.0.0.1");
            message = message.replace(text, STR."[url=\{url}]\{text}[/url]");
        }

        TextFlow text = BBCodeParser.createFormattedText(STR."\{message}");
        // 处理链接
        setLink(text);

        Platform.runLater(() -> log.textArea.getChildren().add(text));
    }

//==========================================={ 私有方法 }===================================================

    /**
     * 将密码标记为link，一起处理点击事件
     *
     * @param msg 输出信息
     * @return 处理后的信息
     */
    private static String setPwdText(String msg) {
        if (!ReUtil.isMatch(AListManager.PASSWORD_MSG_REG, msg)) return msg;

        String password = ReUtil.get(AListManager.PASSWORD_MSG_REG, msg, 2);
        AListManager.tmpPassword(password);
        return msg.replace(password, STR."[url=\{password}]\{password}[/url]");
    }

    /**
     * 处理文本流中的链接
     *
     * @param text 文本流
     */
    private static void setLink(TextFlow text) {

        text.getChildren().forEach(child -> {
            switch (child) {
                case Hyperlink link -> link.setOnAction(_ -> {
                    String linkText = link.getUserData().toString();
                    if (ReUtil.isMatch(URL_IP_REX, linkText)) {
                        Context.getApplication().getHostServices().showDocument(linkText);
                    } else {
                        ClipboardUtil.setStr(linkText);
                        var pop = new Popover(new Text(Context.getLanguageBinding("msg.alist.pwd.copy").get()));
                        pop.show(link);
                    }
                    link.setVisited(false);
                });
                case TextFlow flow -> setLink(flow);
                default -> {
                }
            }
        });
    }

    /**
     * 控制台输出颜色
     *
     * @param msg 输出消息
     * @return bbcode 颜色文本
     */
    private static String resetConsoleColor(String msg) {
        if (!msg.contains(CONSOLE_COLOR_PREFIX) || !Pattern.matches(CONSOLE_MSG_REX, msg)) return msg;

        // 多颜色处理
        String[] split = Pattern.compile("\\033\\[(\\d;)?(\\d+)m")
                .matcher(msg)
                .replaceAll(matchResult -> "\n" + matchResult.group())
                .replaceFirst("\n", "")
                .split("\n");

        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile("\\033\\[(\\d;)?(\\d+)m(.*)");
        Matcher matcher;
        for (int i = 0; i < split.length; i++) {
            matcher = pattern.matcher(split[i]);
            if(!matcher.matches()) continue;

            if (i % 2 == 0) {
                String color = StringUtils.lowerCase(Color.valueOf(Integer.parseInt(matcher.group(2))).getColor());
                sb.append(color(color, matcher.group(3)));
            } else {
                sb.append(matcher.group(3));
            }
        }

        return sb.toString();
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
        WARN("WARN", "-color-danger-emphasis"),
        ERROR("ERROR", "-color-danger-fg"),
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
