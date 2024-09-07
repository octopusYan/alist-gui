package cn.octopusyan.alistgui.controller;

import cn.octopusyan.alistgui.base.BaseController;
import cn.octopusyan.alistgui.config.Context;
import com.gluonhq.emoji.EmojiData;
import com.gluonhq.emoji.util.EmojiImageUtils;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * 主页面控制器
 *
 * @author octopus_yan@foxmail.com
 */
public class RootController extends BaseController<VBox> {

    private double xOffset;
    private double yOffset;

    // 布局
    @FXML
    private VBox rootPane;
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

    /**
     * 获取根布局
     *
     * @return 根布局对象
     */
    @Override
    public VBox getRootPanel() {
        return rootPane;
    }

    /**
     * 初始化数据
     */
    @Override
    public void initData() {
        tabPane.getSelectionModel().select(Context.getCurrentViewIndex());
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

        windowHeader.setOnMousePressed(event -> {
            xOffset = getWindow().getX() - event.getScreenX();
            yOffset = getWindow().getY() - event.getScreenY();
        });
        windowHeader.setOnMouseDragged(event -> {
            getWindow().setX(event.getScreenX() + xOffset);
            getWindow().setY(event.getScreenY() + yOffset);
        });

        tabPane.getSelectionModel()
                .selectedIndexProperty()
                .addListener((_, _, newValue) -> Context.setCurrentViewIndex(newValue));
    }
}
