package cn.octopusyan.alistgui.base;

import cn.octopusyan.alistgui.Application;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.config.I18n;
import cn.octopusyan.alistgui.util.FxmlUtil;
import cn.octopusyan.alistgui.util.ViewUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 通用视图控制器基类
 *
 * @author octopus_yan@foxmail.com
 */
public abstract class BaseController<VM extends BaseViewModel> implements Initializable {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Getter
    protected final VM viewModel;

    public BaseController() {
        //初始化时保存当前Controller实例
        Context.getControllers().put(this.getClass().getSimpleName(), this);

        // view model
        VM vm = null;
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType type) {
            Class<VM> clazz = (Class<VM>) type.getActualTypeArguments()[0];
            try {
                vm = clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        viewModel = vm;

    }

    /**
     * 国际化绑定
     */
    private void bindI18n() {
        // i18n 绑定
        try {
            for (Field field : getAllField(this.getClass())) {
                I18n i18n = field.getAnnotation(I18n.class);
                if (i18n != null && StringUtils.isNoneEmpty(i18n.key())) {
                    switch (field.get(this)) {
                        case Labeled labeled -> labeled.textProperty().bind(Context.getLanguageBinding(i18n.key()));
                        case Tab tab -> tab.textProperty().bind(Context.getLanguageBinding(i18n.key()));
                        case MenuItem mi -> mi.textProperty().bind(Context.getLanguageBinding(i18n.key()));
                        default -> {}
                    }
                }
            }
        } catch (IllegalAccessException e) {
            logger.error("获取属性失败", e);
        }
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 全局窗口拖拽
        if (dragWindow() && getRootPanel() != null) {
            // 窗口拖拽
            ViewUtil.bindDragged(getRootPanel());
        }

        // 国际化绑定
        bindI18n();

        // 初始化数据
        initData();

        // 初始化视图样式
        initViewStyle();

        // 初始化视图事件
        initViewAction();
    }

    /**
     * 窗口拖拽设置
     *
     * @return 是否启用
     */
    public boolean dragWindow() {
        return false;
    }

    /**
     * 获取根布局
     *
     * @return 根布局对象
     */
    public abstract Pane getRootPanel();

    /**
     * 获取根布局
     * <p> 搭配 {@link FxmlUtil#load(String)} 使用
     *
     * @return 根布局对象
     */
    protected String getRootFxml() {
        System.out.println(getClass().getSimpleName());
        return "";
    }

    protected Stage getWindow() {
        return Application.getPrimaryStage();
    }

    /**
     * 初始化数据
     */
    public abstract void initData();

    /**
     * 视图样式
     */
    public abstract void initViewStyle();

    /**
     * 视图事件
     */
    public abstract void initViewAction();


    private static List<Field> getAllField(Class<?> class1) {
        List<Field> list = new ArrayList<>();
        while (class1 != Object.class) {
            list.addAll(Arrays.stream(class1.getDeclaredFields()).toList());
            //获取父类
            class1 = class1.getSuperclass();
        }
        return list;
    }

}
