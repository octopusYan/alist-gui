package cn.octopusyan.alistgui.controller;

import cn.octopusyan.alistgui.base.BaseController;
import cn.octopusyan.alistgui.config.ConfigManager;
import cn.octopusyan.alistgui.config.Context;
import cn.octopusyan.alistgui.enums.ProxySetup;
import cn.octopusyan.alistgui.viewModel.SetupViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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
public class SetupController extends BaseController<GridPane> implements Initializable {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @FXML
    public GridPane setupView;
    @FXML
    public CheckBox autoStartCheckBox;
    @FXML
    public CheckBox silentStartupCheckBox;
    @FXML
    public ComboBox<Locale> languageComboBox;
    @FXML
    public ComboBox<ProxySetup> proxySetupComboBox;
//    @FXML
//    public RadioButton noProxy;
//    @FXML
//    public RadioButton systemProxy;
//    @FXML
//    public RadioButton manualProxy;
    @FXML
    public Pane proxySetupPane;
    @FXML
    public TextField proxyHost;
    @FXML
    public TextField proxyPort;
    @FXML
    public Label alistVersion;

    private final SetupViewModel setupViewModel = new SetupViewModel();
    private final ToggleGroup proxySetupGroup = new ToggleGroup();

    @Override
    public GridPane getRootPanel() {
        return setupView;
    }

    @Override
    public void initData() {
        languageComboBox.setItems(FXCollections.observableList(Context.SUPPORT_LANGUAGE_LIST));
        proxySetupComboBox.setItems(FXCollections.observableList(List.of(ProxySetup.values())));

//        noProxy.setToggleGroup(proxySetupGroup);
//        systemProxy.setToggleGroup(proxySetupGroup);
//        manualProxy.setToggleGroup(proxySetupGroup);
    }

    @Override
    public void initViewStyle() {
        proxySetupComboBox.getSelectionModel().selectedItemProperty().addListener(observable -> {
            proxySetupPane.setVisible(ProxySetup.MANUAL.equals(setupViewModel.proxySetupProperty().get()));
        });
        proxySetupComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            proxySetupPane.setVisible(ProxySetup.MANUAL.equals(newValue));
        });

        languageComboBox.getSelectionModel().select(ConfigManager.language());
        proxySetupComboBox.getSelectionModel().select(ConfigManager.proxySetup());
    }

    @Override
    public void initViewAction() {
        autoStartCheckBox.selectedProperty().bindBidirectional(setupViewModel.autoStartProperty());
        silentStartupCheckBox.selectedProperty().bindBidirectional(setupViewModel.silentStartupProperty());
        proxySetupComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setupViewModel.proxySetupProperty().set(newValue);
        });
        proxyHost.textProperty().bindBidirectional(setupViewModel.proxyHostProperty());
        proxyPort.textProperty().bindBidirectional(setupViewModel.proxyPortProperty());
        languageComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setupViewModel.languageProperty().set(newValue);
            logger.info("language changed to {}", newValue);
        });
        alistVersion.textProperty().bindBidirectional(setupViewModel.aListVersionProperty());
    }
}
