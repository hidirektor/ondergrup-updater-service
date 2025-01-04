package me.t3sl4.updater.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Screen;
import javafx.stage.Stage;
import me.t3sl4.updater.utils.FileUtil;
import me.t3sl4.updater.utils.GeneralUtil;
import me.t3sl4.updater.utils.SceneUtil;
import me.t3sl4.updater.utils.SystemVariables;
import me.t3sl4.util.os.OSUtil;

import java.io.IOException;
import java.util.List;

public class Main extends Application {
    List<Screen> screens = Screen.getScreens();

    public static Screen defaultScreen;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FileUtil.criticalFileSystem();

        Platform.setImplicitExit(false);

        if(!System.getProperty("os.name").toLowerCase().contains("win")) {
            if (!GeneralUtil.checkSingleInstance()) {
                System.out.println("Program zaten çalışıyor. Odaklanıyor...");
                Platform.exit();
                return;
            }
        }

        OSUtil.updateLocalVersion(SystemVariables.PREF_NODE_NAME, SystemVariables.PREF_UPDATER_KEY, SystemVariables.getVersion());

        defaultScreen = screens.get(0);
        SceneUtil.openMainScreen(screens.get(0));
    }

    public static void main(String[] args) {
        launch(args);
    }
}