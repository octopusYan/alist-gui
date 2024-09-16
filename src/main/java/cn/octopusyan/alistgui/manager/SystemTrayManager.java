package cn.octopusyan.alistgui.manager;

import cn.octopusyan.alistgui.Application;
import cn.octopusyan.alistgui.config.Constants;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.util.WindowsUtil;
import cn.octopusyan.alistgui.view.PopupMenu;
import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;

/**
 * 系统托盘管理
 *
 * @author octopus_yan
 */
@Slf4j
public class SystemTrayManager {
    // 托盘工具
    private static final SystemTray systemTray;
    private static TrayIcon trayIcon;
    private static PopupMenu popupMenu;

    static {
        //检查系统是否支持托盘
        if (!SystemTray.isSupported()) {
            //系统托盘不支持
            log.info("{}:系统托盘不支持", Thread.currentThread().getStackTrace()[1].getClassName());
            systemTray = null;
        } else {
            systemTray = SystemTray.getSystemTray();
        }
    }

    public static void toolTip(String toptip) {
        if (trayIcon == null) return;

        trayIcon.setToolTip(toptip);
    }

    public static void icon(String path) {
        if (trayIcon == null) return;
        icon(WindowsUtil.class.getResource(path));
    }

    public static void icon(URL url) {
        if (trayIcon == null) return;
        icon(Toolkit.getDefaultToolkit().getImage(url));
    }

    public static void icon(Image image) {
        if (trayIcon == null) return;
        trayIcon.setImage(image);
    }

    public static boolean isShowing() {
        if (systemTray == null) return false;

        return List.of(systemTray.getTrayIcons()).contains(trayIcon);
    }

    public static void show() {

        // 是否启用托盘
        if (!ConfigManager.closeToTray() || systemTray == null) {
            if (trayIcon != null && isShowing()) {
                hide();
            }
            return;
        }

        initTrayIcon();

        try {
            if (!isShowing())
                systemTray.add(trayIcon);
        } catch (AWTException e) {
            //系统托盘添加失败
            log.error("{}:系统添加失败", Thread.currentThread().getStackTrace()[1].getClassName(), e);
        }
    }

    public static void hide() {
        if (systemTray == null) return;

        systemTray.remove(trayIcon);
    }

//========================================={ private }===========================================

    private static void initTrayIcon() {
        if (trayIcon != null) return;

        // 系统托盘图标
        Image image = Toolkit.getDefaultToolkit().getImage(WindowsUtil.class.getResource("/assets/logo-disabled.png"));
        trayIcon = new TrayIcon(image);

        // 设置图标尺寸自动适应
        trayIcon.setImageAutoSize(true);

        // 弹出式菜单组件
//        trayIcon.setPopupMenu(getMenu());

        // 鼠标移到系统托盘,会显示提示文本
        toolTip(Constants.APP_TITLE);

        // 鼠标监听
        trayIcon.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent event) {
                maybeShowPopup(event);
            }

            @Override
            public void mousePressed(MouseEvent event) {
                maybeShowPopup(event);
            }

            private void maybeShowPopup(MouseEvent event) {
                // popup menu trigger event
                if (event.isPopupTrigger()) {
                    // 弹出菜单
                    Platform.runLater(() -> {
                        initPopupMenu();
                        popupMenu.show(event);
                    });
                } else if (event.getButton() == MouseEvent.BUTTON1) {
                    // 显示 PrimaryStage
                    Platform.runLater(() -> Application.getPrimaryStage().show());
                }
            }
        });
    }

    /**
     * 构建托盘菜单
     */
    private static void initPopupMenu() {
        if (popupMenu != null) return;

        MenuItem start = PopupMenu.menuItem(getString("main.control.start"), _ -> AListManager.openScheme());
        MenuItem browser = PopupMenu.menuItem(getString("main.more.browser"), _ -> AListManager.openScheme());
        browser.setDisable(true);

        AListManager.runningProperty().addListener((_, _, newValue) -> {
            start.setText(getString(STR."main.control.\{newValue ? "stop" : "start"}"));
            browser.disableProperty().set(!newValue);
            toolTip(STR."AList \{newValue ? "running" : "stopped"}");
            icon(STR."/assets/logo\{newValue ? "" : "-disabled"}.png");
        });

        popupMenu = new PopupMenu()
                .addItem(new MenuItem(Constants.APP_TITLE), _ -> stage().show())
                .addSeparator()
                .addCaptionItem("AList")
                .addItem(start, _ -> {
                    if (AListManager.isRunning()) {
                        AListManager.stop();
                    } else {
                        AListManager.start();
                    }
                })
                .addItem(getString("main.control.restart"), _ -> AListManager.restart())
                .addMenu(getString("main.control.more"), browser,
                        PopupMenu.menuItem(getString("main.more.open-config"), _ -> AListManager.openConfig()),
                        PopupMenu.menuItem(getString("main.more.open-log"), _ -> AListManager.openLogFolder()))
                .addSeparator()
                .addExitItem();
    }

    private static String getString(String key) {
        return Context.getLanguageBinding(key).get();
    }

    private static Stage stage() {
        return WindowsUtil.getStage();
    }
}
