package cn.octopusyan.alistgui.controller;

import atlantafx.base.theme.Theme;
import cn.octopusyan.alistgui.base.BaseController;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.enums.ProxySetup;
import cn.octopusyan.alistgui.manager.ConfigManager;
import cn.octopusyan.alistgui.viewModel.SetupViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

/**
 * 设置页面控制器
 *
 * @author octopus_yan
 */
public class SetupController extends BaseController<SetupViewModel> implements Initializable {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @FXML
    public VBox setupView;
    @FXML
    public CheckBox autoStartCheckBox;
    @FXML
    public CheckBox silentStartupCheckBox;
    @FXML
    public CheckBox closeToTrayCheckBox;
    @FXML
    public ComboBox<Locale> languageComboBox;
    @FXML
    public ComboBox<Theme> themeComboBox;
    @FXML
    public ComboBox<ProxySetup> proxySetupComboBox;
    @FXML
    public Pane proxySetupPane;
    @FXML
    public Button proxyCheck;
    @FXML
    public TextField proxyHost;
    @FXML
    public TextField proxyPort;

    @Override
    public VBox getRootPanel() {
        return setupView;
    }

    @Override
    public void initData() {
        languageComboBox.setItems(FXCollections.observableList(Context.SUPPORT_LANGUAGE_LIST));
        themeComboBox.setItems(FXCollections.observableList(ConfigManager.THEME_LIST));
        proxySetupComboBox.setItems(FXCollections.observableList(List.of(ProxySetup.values())));

        themeComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Theme object) {
                return object.getName();
            }

            @Override
            public Theme fromString(String string) {
                return ConfigManager.THEME_MAP.get(string);
            }
        });
    }

    @Override
    public void initViewStyle() {
        proxySetupComboBox.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            proxySetupPane.setVisible(ProxySetup.MANUAL.equals(newValue));
            proxyCheck.setVisible(!ProxySetup.NO_PROXY.equals(newValue));
        });

        languageComboBox.getSelectionModel().select(ConfigManager.language());
        themeComboBox.getSelectionModel().select(ConfigManager.theme());
        proxySetupComboBox.getSelectionModel().select(ConfigManager.proxySetup());
    }

    @Override
    public void initViewAction() {
        //
        autoStartCheckBox.selectedProperty().bindBidirectional(viewModel.autoStartProperty());
        silentStartupCheckBox.selectedProperty().bindBidirectional(viewModel.silentStartupProperty());
        closeToTrayCheckBox.selectedProperty().bindBidirectional(viewModel.closeToTrayProperty());
        proxyHost.textProperty().bindBidirectional(viewModel.proxyHostProperty());
        proxyPort.textProperty().bindBidirectional(viewModel.proxyPortProperty());

        viewModel.languageProperty().bind(languageComboBox.getSelectionModel().selectedItemProperty());
        viewModel.themeProperty().bind(themeComboBox.getSelectionModel().selectedItemProperty());
        viewModel.proxySetupProperty().bind(proxySetupComboBox.getSelectionModel().selectedItemProperty());
    }

    @FXML
    public void proxyTest() {
        viewModel.proxyTest();
    }
}
