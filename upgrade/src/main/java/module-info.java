module cn.octopusyan.upgrade {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires cn.hutool.core;
    requires static lombok;
    requires org.apache.commons.lang3;


    opens cn.octopusyan.upgrade to javafx.fxml;
    exports cn.octopusyan.upgrade;
    exports cn.octopusyan.upgrade.util;
    opens cn.octopusyan.upgrade.util to javafx.fxml;
    exports cn.octopusyan.upgrade.view;
    opens cn.octopusyan.upgrade.view to javafx.fxml;
}