package cn.octopusyan.alistgui.controller;

import atlantafx.base.controls.Popover;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.octopusyan.alistgui.base.BaseController;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.config.I18n;
import cn.octopusyan.alistgui.manager.AListManager;
import cn.octopusyan.alistgui.viewModel.AdminPanelViewModel;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.apache.commons.lang3.StringUtils;

/**
 * 管理员密码
 *
 * @author octopus_yan
 */
public class PasswordController extends BaseController<AdminPanelViewModel> {
    public AnchorPane adminPanel;

    @I18n(key = "admin.pwd.toptip")
    public Label toptip;
    @I18n(key = "admin.pwd.user-field")
    public Label usernameLabel;
    public TextField usernameField;
    @FXML
    public Button copyUsername;
    @I18n(key = "admin.pwd.pwd-field")
    public Label passwordLabel;
    public PasswordField passwordField;
    public Button refreshPassword;
    public Button savePassword;
    public Button copyPassword;

    private RootController root;

    private final Popover pop = new Popover(new Text(Context.getLanguageBinding("msg.alist.pwd.copy").get()));

    @Override
    public Pane getRootPanel() {
        return adminPanel;
    }

    @Override
    public void initData() {
        root = (RootController) Context.getControllers().get("RootController");
    }

    @Override
    public void initViewStyle() {
        pop.setArrowLocation(Popover.ArrowLocation.BOTTOM_CENTER);
    }

    @Override
    public void initViewAction() {
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
        passwordField.setOnMouseClicked(event -> {
            // 点击密码框时，设置为可修改状态
            passwordField.setEditable(true);
            refreshPassword.setVisible(true);
            refreshPassword.setManaged(true);
        });
        ChangeListener<Boolean> changeListener = (_, _, focused) -> {
            if (!focused && !refreshPassword.isFocused()
                    && !copyPassword.isFocused()
                    && StringUtils.equals(passwordField.getText(), AListManager.passwordProperty().get())) {
                // 当密码栏失去焦点，如果密码未变更，设置为不可用状态
                passwordField.setEditable(false);
                refreshPassword.setVisible(false);
                refreshPassword.setManaged(false);
                savePassword.setVisible(false);
                savePassword.setManaged(false);
            }
        };
        passwordField.focusedProperty().addListener(changeListener);
        refreshPassword.focusedProperty().addListener(changeListener);
        savePassword.focusedProperty().addListener(changeListener);
        copyPassword.focusedProperty().addListener(changeListener);
        // 监听密码修改，展示保存按钮
        passwordField.textProperty().addListener((_, _, newValue) -> {
            boolean equals = StringUtils.equals(newValue, AListManager.passwordProperty().get());
            savePassword.setVisible(!equals);
            savePassword.setManaged(!equals);
        });
    }

    public void show() {
        root.showModal(getRootPanel(), true);
    }

    @FXML
    public void close() {
        passwordField.setText(AListManager.passwordProperty().get());
        root.hideModal();
    }

    @FXML
    public void copyUsername() {
        usernameField.copy();
        pop.show(copyUsername);
    }

    @FXML
    public void savePassword(ActionEvent event) {
        Object source = event.getSource();
        if (refreshPassword.equals(source)) {
            AListManager.resetPassword();
            return;
        }
        AListManager.resetPassword(passwordField.getText());
        savePassword.setVisible(false);
        savePassword.setManaged(false);
    }

    @FXML
    public void copyPassword() {
        ClipboardUtil.setStr(AListManager.password());
        pop.show(copyPassword);
    }
}
