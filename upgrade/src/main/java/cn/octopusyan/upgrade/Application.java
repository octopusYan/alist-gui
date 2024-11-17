package cn.octopusyan.upgrade;

import atlantafx.base.theme.PrimerLight;
import cn.octopusyan.upgrade.util.FxmlUtil;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        // 主题样式
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        // 启动主界面
        Pane root = FxmlUtil.init("/fxml/hello-view.fxml").load();
        Scene scene = new Scene(root, 420, 240);
        primaryStage.setTitle(ResourceBundle.getBundle("language/language").getString("title"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // 关闭主界面
        Platform.exit();
        System.exit(0);
    }
}