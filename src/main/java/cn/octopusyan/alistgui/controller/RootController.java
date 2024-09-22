package cn.octopusyan.alistgui.controller;

import atlantafx.base.controls.ModalPane;
import cn.octopusyan.alistgui.base.BaseController;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.config.I18n;
import cn.octopusyan.alistgui.manager.ConfigManager;
import cn.octopusyan.alistgui.manager.SystemTrayManager;
import cn.octopusyan.alistgui.util.ViewUtil;
import cn.octopusyan.alistgui.viewModel.RootViewModel;
import com.gluonhq.emoji.EmojiData;
import com.gluonhq.emoji.util.EmojiImageUtils;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Locale;

/**
 * Root 页面控制器
 *
 * @author octopus_yan@foxmail.com
 */
public class RootController extends BaseController<RootViewModel> {
    // 布局
    public StackPane rootPane;
    public HBox windowHeader;
    public FontIcon alwaysOnTopIcon;
    public FontIcon minimizeIcon;
    public FontIcon closeIcon;

    // 界面
    public TabPane tabPane;

    @I18n(key = "root.tab.main")
    public Tab mainTab;
    @I18n(key = "root.tab.setup")
    public Tab setupTab;
    @I18n(key = "root.tab.about")
    public Tab aboutTab;

    // footer
    @I18n(key = "root.foot.doc")
    public Button document;
    @I18n(key = "root.foot.github")
    public Button github;
    @I18n(key = "root.foot.sponsor")
    public Button sponsor;

    private final ModalPane modalPane = new ModalPane();

    /**
     * 获取根布局
     *
     * @return 根布局对象
     */
    @Override
    public StackPane getRootPanel() {
        return rootPane;
    }

    /**
     * 初始化数据
     */
    @Override
    public void initData() {
        tabPane.getSelectionModel().select(viewModel.currentViewIndexProperty().get());
    }

    /**
     * 视图样式
     */
    @Override
    public void initViewStyle() {
        // 设置图标
        EmojiData.emojiFromShortName("book").ifPresent(icon -> {
            ImageView book = EmojiImageUtils.emojiView(icon, 25);
            document.setGraphic(book);
        });
        EmojiData.emojiFromShortName("cat").ifPresent(icon -> {
            ImageView githubIcon = EmojiImageUtils.emojiView(icon, 25);
            github.setGraphic(githubIcon);

        });
        EmojiData.emojiFromShortName("tropical_drink").ifPresent(icon -> {
            ImageView juice = EmojiImageUtils.emojiView(icon, 25);
            sponsor.setGraphic(juice);
        });

        // 遮罩
        getRootPanel().getChildren().add(modalPane);
        modalPane.setId("modalPane");
        // reset side and transition to reuse a single modal pane between different examples
        modalPane.displayProperty().addListener((obs, old, val) -> {
            if (!val) {
                modalPane.setAlignment(Pos.CENTER);
                modalPane.usePredefinedTransitionFactories(null);
            }
        });
    }

    /**
     * 视图事件
     */
    @Override
    public void initViewAction() {
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> {
            if (ConfigManager.closeToTray()) {
                SystemTrayManager.show();
            } else {
                SystemTrayManager.hide();
            }
            Platform.setImplicitExit(!ConfigManager.closeToTray());
            getWindow().close();
        });
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> getWindow().setIconified(true));
        alwaysOnTopIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> {
            boolean newVal = !getWindow().isAlwaysOnTop();
            alwaysOnTopIcon.pseudoClassStateChanged(PseudoClass.getPseudoClass("always-on-top"), newVal);
            getWindow().setAlwaysOnTop(newVal);
        });

        ViewUtil.bindDragged(windowHeader);

        viewModel.currentViewIndexProperty().bind(tabPane.getSelectionModel().selectedIndexProperty());
    }

    public void openDocument() {
        String locale = Context.getCurrentLocale().equals(Locale.ENGLISH) ? "" : "zh/";
        Context.openUrl(STR."https://alist.nn.ci/\{locale}");
    }

    public void openGithub() {
        Context.openUrl("https://github.com/alist-org/alist");
    }

    public void showTab(int index) {
        if (index < 0 || index > 2) return;
        tabPane.getSelectionModel().select(index);
    }

    public void showModal(Node node, boolean persistent) {
        modalPane.show(node);
        modalPane.setPersistent(persistent);
    }

    public void hideModal() {
        modalPane.hide(false);
        modalPane.setPersistent(false);
    }
}
