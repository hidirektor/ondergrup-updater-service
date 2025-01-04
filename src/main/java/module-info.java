module me.t3sl4.updater {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.management;
    requires com.google.common;
    requires me.t3sl4.util.version;
    requires me.t3sl4.util.os;


    opens me.t3sl4.updater to javafx.fxml;
    exports me.t3sl4.updater;
    opens me.t3sl4.updater.app to javafx.fxml;
    exports me.t3sl4.updater.app;
    opens me.t3sl4.updater.controller to javafx.fxml;
    exports me.t3sl4.updater.controller;
}