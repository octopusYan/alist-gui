package cn.octopusyan.alistgui.controller;

import atlantafx.base.controls.ModalPane;
import cn.octopusyan.alistgui.base.BaseController;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.util.WindowsUtil;
import cn.octopusyan.alistgui.viewModel.RootViewModel;
import com.gluonhq.emoji.EmojiData;
import com.gluonhq.emoji.util.EmojiImageUtils;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
    @FXML
    private StackPane rootPane;
    @FXML
    private HBox windowHeader;
    @FXML
    private FontIcon alwaysOnTopIcon;
    @FXML
    private FontIcon minimizeIcon;
    @FXML
    private FontIcon closeIcon;

    // 界面
    @FXML
    private TabPane tabPane;

    // footer
    @FXML
    public Button document;
    @FXML
    public Button github;
    @FXML
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
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> getWindow().close());
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> getWindow().setIconified(true));
        alwaysOnTopIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> {
            boolean newVal = !getWindow().isAlwaysOnTop();
            alwaysOnTopIcon.pseudoClassStateChanged(PseudoClass.getPseudoClass("always-on-top"), newVal);
            getWindow().setAlwaysOnTop(newVal);
        });

        WindowsUtil.bindDragged(windowHeader);

        viewModel.currentViewIndexProperty().bind(tabPane.getSelectionModel().selectedIndexProperty());
    }

    @FXML
    public void openDocument() {
        String locale = Context.getCurrentLocale().equals(Locale.ENGLISH) ? "" : "zh/";
        Context.openUrl(STR."https://alist.nn.ci/\{locale}");
    }

    @FXML
    public void openGithub() {
        Context.openUrl("https://github.com/alist-org/alist");
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
