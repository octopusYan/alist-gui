package cn.octopusyan.alistgui.config;

import cn.octopusyan.alistgui.base.BaseController;
import cn.octopusyan.alistgui.controller.MainController;
import cn.octopusyan.alistgui.controller.RootController;
import cn.octopusyan.alistgui.controller.SetupController;
import cn.octopusyan.alistgui.util.FxmlUtil;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
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
    /**
     * 控制器集合
     */
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
    public static final List<Locale> SUPPORT_LANGUAGE_LIST = Arrays.asList(Locale.CHINESE, Locale.ENGLISH);
    /**
     * 记录当前所选时区
     */
    private static final ObjectProperty<Locale> currentLocale = new SimpleObjectProperty<>();


    private Context() {
        throw new IllegalStateException("Utility class");
    }

    // 获取控制器集合
    public static Map<String, BaseController<?>> getControllers() {
        return controllers;
    }

    // 获取控制工厂
    public static Callback<Class<?>, Object> getControlFactory() {
        return type -> {
            if (type.equals(RootController.class)) {
                return new RootController();
            } else if (type.equals(MainController.class)) {
                return new MainController();
            } else if (type.equals(SetupController.class)) {
                return new SetupController();
            }
            throw new IllegalStateException("Unexpected value: " + type);
        };
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
        ConfigManager.language(locale);
        LANGUAGE_RESOURCE_FACTORY.setResourceBundle(ResourceBundle.getBundle(LANGUAGE_RESOURCE_NAME, locale));
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
        currentLocaleProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                try {
                    loadScene();
                } catch (IOException e) {
                    log.error("", e);
                }
            });
        });
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
        Parent root = loader.load();//底层面板
//        bindShadow((Pane) root);
        Optional.ofNullable(scene).ifPresentOrElse(
                s -> s.setRoot(root),
                () -> {
                    scene = new Scene(root);
                    URL resource = Objects.requireNonNull(Context.class.getResource("/css/root.css"));
                    scene.getStylesheets().addAll(resource.toExternalForm());
                    scene.setFill(Color.TRANSPARENT);
                }
        );
    }

    // 设置当前展示的界面
    public static void setCurrentViewIndex(Number newValue) {
        currentViewIndexProperty.setValue(newValue);
    }

    // 获取当前展示的界面Index
    public static Integer getCurrentViewIndex() {
        return currentViewIndexProperty.get();
    }

    private static void bindShadow(Pane pane) {
        Pane root = new Pane();
        root.setPadding(new Insets(20, 20, 20, 20));
        DropShadow dropshadow = new DropShadow();// 阴影向外
        dropshadow.setRadius(10);// 颜色蔓延的距离
        dropshadow.setOffsetX(0);// 水平方向，0则向左右两侧，正则向右，负则向左
        dropshadow.setOffsetY(0);// 垂直方向，0则向上下两侧，正则向下，负则向上
        dropshadow.setSpread(0.1);// 颜色变淡的程度
        dropshadow.setColor(Color.BLACK);// 设置颜色
        root.setEffect(dropshadow);// 绑定指定窗口控件
        root.getChildren().addAll(pane);
    }
}