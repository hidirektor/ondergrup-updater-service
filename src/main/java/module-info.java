module me.t3sl4.updater {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.prefs;
    requires org.json;
    requires java.net.http;
    requires java.management;
    requires com.google.common;


    opens me.t3sl4.updater to javafx.fxml;
    exports me.t3sl4.updater;
    opens me.t3sl4.updater.app to javafx.fxml;
    exports me.t3sl4.updater.app;
    opens me.t3sl4.updater.controller to javafx.fxml;
    exports me.t3sl4.updater.controller;
}