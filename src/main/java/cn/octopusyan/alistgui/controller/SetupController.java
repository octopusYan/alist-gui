package cn.octopusyan.alistgui.controller;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * 设置页面控制器
 *
 * @author octopus_yan
 */
public class SetupController extends BaseController<VBox> implements Initializable {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @FXML
    public VBox setupView;
    @FXML
    public CheckBox autoStartCheckBox;
    @FXML
    public CheckBox silentStartupCheckBox;
    @FXML
    public ComboBox<Locale> languageComboBox;
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

    private final SetupViewModel viewModule = new SetupViewModel();

    @Override
    public VBox getRootPanel() {
        return setupView;
    }

    @Override
    public void initData() {
        languageComboBox.setItems(FXCollections.observableList(Context.SUPPORT_LANGUAGE_LIST));
        proxySetupComboBox.setItems(FXCollections.observableList(List.of(ProxySetup.values())));

    }

    @Override
    public void initViewStyle() {
        proxySetupComboBox.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            proxySetupPane.setVisible(ProxySetup.MANUAL.equals(newValue));
            proxyCheck.setVisible(!ProxySetup.NO_PROXY.equals(newValue));
        });

        languageComboBox.getSelectionModel().select(ConfigManager.language());
        proxySetupComboBox.getSelectionModel().select(ConfigManager.proxySetup());
    }

    @Override
    public void initViewAction() {
        autoStartCheckBox.selectedProperty().bindBidirectional(viewModule.autoStartProperty());
        silentStartupCheckBox.selectedProperty().bindBidirectional(viewModule.silentStartupProperty());
        proxySetupComboBox.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> viewModule.proxySetupProperty().set(newValue));
        proxyHost.textProperty().bindBidirectional(viewModule.proxyHostProperty());
        proxyPort.textProperty().bindBidirectional(viewModule.proxyPortProperty());
        languageComboBox.getSelectionModel().selectedItemProperty().subscribe(locale -> {
            viewModule.languageProperty().set(locale);
            logger.info("language changed to {}", locale);
        });
    }

    @FXML
    public void proxyTest() {
        viewModule.proxyTest();
    }
}
