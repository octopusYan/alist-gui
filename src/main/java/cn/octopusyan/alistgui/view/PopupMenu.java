package cn.octopusyan.alistgui.view;

import atlantafx.base.controls.CaptionMenuItem;
import cn.octopusyan.alistgui.config.Constants;
import cn.octopusyan.alistgui.util.WindowsUtil;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 托盘图标 菜单
 *
 * @author octopus_yan
 */
public class PopupMenu {
    // 用来隐藏弹出窗口的任务栏图标
    private static final Stage utilityStage = new Stage();
    // 菜单栏
    private final ContextMenu root = new ContextMenu();

    static {
        utilityStage.initStyle(StageStyle.UTILITY);
        utilityStage.setScene(new Scene(new Region()));
        utilityStage.setOpacity(0);
    }

    public PopupMenu() {

        root.focusedProperty().addListener((_, _, focused) -> {
            if (!focused)
                Platform.runLater(() -> {
                    root.hide();
                    utilityStage.hide();
                });
        });
    }

    public PopupMenu addItem(String label, EventHandler<ActionEvent> handler) {
        return addItem(new MenuItem(label), handler);
    }

    public PopupMenu addItem(StringBinding bind, EventHandler<ActionEvent> handler) {
        MenuItem menuItem = new MenuItem();
        menuItem.textProperty().bind(bind);
        return addItem(menuItem, handler);
    }

    public PopupMenu addItem(MenuItem node, EventHandler<ActionEvent> handler) {
        node.setOnAction(handler);
        return addItem(node);
    }

    public PopupMenu addSeparator() {
        return addItem(new SeparatorMenuItem());
    }

    public PopupMenu addCaptionItem() {
        return addCaptionItem(null);
    }

    public PopupMenu addCaptionItem(String title) {
        return addItem(new CaptionMenuItem(title));
    }

    public PopupMenu addMenu(String label, MenuItem... items) {
        return addMenu(new Menu(label), items);
    }

    public PopupMenu addMenu(StringBinding label, MenuItem... items) {
        Menu menu = new Menu();
        menu.textProperty().bind(label);
        return addMenu(menu, items);
    }

    public PopupMenu addMenu(Menu menu, MenuItem... items) {
        menu.getItems().addAll(items);
        return addItem(menu);
    }

    public PopupMenu addTitleItem() {
        return addTitleItem(Constants.APP_TITLE);
    }

    public PopupMenu addTitleItem(String label) {
        return addExitItem(label);
    }

    public PopupMenu addExitItem() {
        return addExitItem("Exit");
    }

    public PopupMenu addExitItem(String label) {
        return addItem(label, _ -> Platform.exit());
    }

    private PopupMenu addItem(MenuItem node) {
        root.getItems().add(node);
        return this;
    }

    public void show(java.awt.event.MouseEvent event) {
        // 必须调用show才会隐藏任务栏图标
        utilityStage.show();

        if (root.isShowing())
            root.hide();

        root.show(utilityStage,
                event.getX() / WindowsUtil.scaleX,
                event.getY() / WindowsUtil.scaleY
        );
        // 获取焦点 (失去焦点隐藏自身)
        root.requestFocus();
    }

    public static MenuItem menuItem(String label, EventHandler<ActionEvent> handler) {
        MenuItem menuItem = new MenuItem(label);
        menuItem.setOnAction(handler);
        return menuItem;
    }

    public static MenuItem menuItem(StringBinding stringBinding, EventHandler<ActionEvent> handler) {
        MenuItem menuItem = new MenuItem();
        menuItem.textProperty().bind(stringBinding);
        menuItem.setOnAction(handler);
        return menuItem;
    }
}
