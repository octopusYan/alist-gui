module cn.octopusyan.alistgui {
    requires java.net.http;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.apache.commons.exec;
    requires org.slf4j;
    requires ch.qos.logback.core;
    requires ch.qos.logback.classic;
    requires com.alibaba.fastjson2;

    exports cn.octopusyan.alistgui;
    opens cn.octopusyan.alistgui to javafx.fxml;
    opens cn.octopusyan.alistgui.controller to javafx.fxml;
}