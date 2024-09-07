package cn.octopusyan.alistgui.controller;

import cn.octopusyan.alistgui.base.BaseController;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.enums.ProxySetup;
import cn.octopusyan.alistgui.manager.ConfigManager;
import cn.octopusyan.alistgui.viewModel.SetupViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

/**
 * 设置页面控制器
 *
 * @author octopus_yan
 */
public class SetupController extends BaseController<AnchorPane> implements Initializable {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @FXML
    public AnchorPane setupView;
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
    @FXML
    public Label aListVersion;
    @FXML
    public Button checkAppVersion;

    private final SetupViewModel setupViewModel = new SetupViewModel();

    @Override
    public AnchorPane getRootPanel() {
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
        autoStartCheckBox.selectedProperty().bindBidirectional(setupViewModel.autoStartProperty());
        silentStartupCheckBox.selectedProperty().bindBidirectional(setupViewModel.silentStartupProperty());
        proxySetupComboBox.getSelectionModel().selectedItemProperty()
                .addListener((_, _, newValue) -> setupViewModel.proxySetupProperty().set(newValue));
        proxyHost.textProperty().bindBidirectional(setupViewModel.proxyHostProperty());
        proxyPort.textProperty().bindBidirectional(setupViewModel.proxyPortProperty());
        languageComboBox.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            setupViewModel.languageProperty().set(newValue);
            logger.info("language changed to {}", newValue);
        });
        aListVersion.textProperty().bindBidirectional(setupViewModel.aListVersionProperty());
    }

    @FXML
    public void checkAListUpdate() {
        setupViewModel.checkAListUpdate();
    }
}
