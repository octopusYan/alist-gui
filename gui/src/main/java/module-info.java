module cn.octopusyan.alistgui {
    requires java.desktop;
    requires java.net.http;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.apache.commons.lang3;
    requires org.apache.commons.exec;
    requires org.slf4j;
    requires ch.qos.logback.core;
    requires ch.qos.logback.classic;
    requires cn.hutool.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;
    requires com.gluonhq.emoji;
    requires static lombok;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires atlantafx.base;

    exports cn.octopusyan.alistgui;
    opens cn.octopusyan.alistgui to javafx.fxml;
    opens cn.octopusyan.alistgui.model to com.fasterxml.jackson.databind;
    opens cn.octopusyan.alistgui.controller to javafx.fxml;
    opens cn.octopusyan.alistgui.base to com.fasterxml.jackson.databind;
    opens cn.octopusyan.alistgui.model.upgrade to com.fasterxml.jackson.databind;
    exports cn.octopusyan.alistgui.model.upgrade;
}