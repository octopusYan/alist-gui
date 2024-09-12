package cn.octopusyan.alistgui.config;

import atlantafx.base.theme.Theme;
import cn.octopusyan.alistgui.base.BaseController;
import cn.octopusyan.alistgui.controller.AboutController;
import cn.octopusyan.alistgui.controller.MainController;
import cn.octopusyan.alistgui.controller.RootController;
import cn.octopusyan.alistgui.controller.SetupController;
import cn.octopusyan.alistgui.manager.ConfigManager;
import cn.octopusyan.alistgui.manager.ConsoleLog;
import cn.octopusyan.alistgui.util.FxmlUtil;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * test contect
 *
 * @author octopus_yan
 */
public class Context {
    private static final Logger log = LoggerFactory.getLogger(Context.class);
    private static Scene scene;
    private static final IntegerProperty currentViewIndexProperty = new SimpleIntegerProperty(0);
    private static final ObjectProperty<Theme> theme = new SimpleObjectProperty<>(ConfigManager.theme());

    /**
     * 控制器集合
     */
    @Getter
    private static final Map<String, BaseController<?>> controllers = new HashMap<>();
    /**
     * 默认语言文件 Base Name
     */
    private static final String LANGUAGE_RESOURCE_NAME = "language/language";
    /**
     * 语言资源工厂
     */
    private static final ObservableResourceBundleFactory LANGUAGE_RESOURCE_FACTORY = new ObservableResourceBundleFactory();
    /**
     * 支持的语言集合，应与语言资源文件同步手动更新
     */
    public static final List<Locale> SUPPORT_LANGUAGE_LIST = Arrays.asList(Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH);
    /**
     * 记录当前所选时区
     */
    private static final ObjectProperty<Locale> currentLocale = new SimpleObjectProperty<>();


    private Context() {
        throw new IllegalStateException("Utility class");
    }

    // 获取控制工厂
    public static Callback<Class<?>, Object> getControlFactory() {
        return type -> {
            try {
                return switch (type.getDeclaredConstructor().newInstance()) {
                    case RootController root -> root;
                    case MainController main -> main;
                    case SetupController setup -> setup;
                    case AboutController about -> about;
                    default -> throw new IllegalStateException(STR."Unexpected value: \{type}");
                };
            } catch (Exception e) {
                log.error("", e);
                return null;
            }
        };
    }

    public static ObjectProperty<Theme> themeProperty() {
        return theme;
    }

    // 获取当前所选时区属性
    public static ObjectProperty<Locale> currentLocaleProperty() {
        return currentLocale;
    }

    // 设置当前所选时区
    public static void setCurrentLocale(Locale locale) {
        currentLocaleProperty().set(locale);
    }

    /**
     * 更换语言的组件使用此方法初始化自己的值，调用 {@link Context#setLanguage(Locale)} 来更新界面语言
     *
     * @return 当前界面语言
     */
    // 获取当前界面语言
    public static Locale getCurrentLocale() {
        return currentLocaleProperty().get();
    }

    /**
     * 更新界面语言
     *
     * @param locale 区域
     */
    // 更新界面语言
    public static void setLanguage(Locale locale) {
        setCurrentLocale(locale);
        Locale.setDefault(locale);
        ConfigManager.language(locale);
        LANGUAGE_RESOURCE_FACTORY.setResourceBundle(ResourceBundle.getBundle(LANGUAGE_RESOURCE_NAME, locale));

        log.info("language changed to {}", locale);
        if (ConsoleLog.isInit())
            ConsoleLog.info(STR."language changed to \{locale}");
    }

    /**
     * 获取指定标识的字符串绑定
     *
     * @param key 标识
     * @return 对应该标识的字符串属性绑定
     */
    // 获取指定标识的字符串绑定
    public static StringBinding getLanguageBinding(String key) {
        return LANGUAGE_RESOURCE_FACTORY.getStringBinding(key);
    }

    /**
     * 获取语言资源属性
     */
    public static ObjectProperty<ResourceBundle> getLanguageResource() {
        return LANGUAGE_RESOURCE_FACTORY.getResourceBundleProperty();
    }

    /**
     * 初始化 语言
     */
    private static void initI18n() {
        currentLocaleProperty().addListener((_, _, locale) -> Platform.runLater(() -> {
            try {
                loadScene();
            } catch (IOException e) {
                log.error("", e);
            }
        }));
    }

    /**
     * 有此类所在路径决定相对路径
     *
     * @param path 资源文件相对路径
     * @return 资源文件路径
     */
    // 加载资源文件
    public static URL load(String path) {
        return Context.class.getResource(path);
    }

    /**
     * 初始化场景
     *
     * @return Scene
     * @throws IOException 如果在加载过程中发生错误
     */
    public static Scene initScene() throws IOException {
        initI18n();
        loadScene();
        return scene;
    }

    private static void loadScene() throws IOException {
        FXMLLoader loader = FxmlUtil.load("root-view");
        loader.setControllerFactory(Context.getControlFactory());
        //底层面板
        Pane root = loader.load();
        Optional.ofNullable(scene).ifPresentOrElse(
                s -> s.setRoot(root),
                () -> {
                    scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 20, Color.TRANSPARENT);
                    URL resource = Objects.requireNonNull(Context.class.getResource("/css/root-view.css"));
                    scene.getStylesheets().addAll(resource.toExternalForm());
                    scene.setFill(Color.TRANSPARENT);
                }
        );
    }

    /**
     * 设置当前展示的界面
     *
     * @param index 界面Index
     */
    public static void setCurrentViewIndex(Number index) {
        currentViewIndexProperty.setValue(index);
    }

    /**
     * 获取当前展示的界面Index
     *
     * @return 界面Index
     */
    public static Integer getCurrentViewIndex() {
        return currentViewIndexProperty.get();
    }
}