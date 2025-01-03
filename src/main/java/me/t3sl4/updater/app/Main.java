package me.t3sl4.updater.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Screen;
import javafx.stage.Stage;
import me.t3sl4.updater.utils.FileUtil;
import me.t3sl4.updater.utils.GeneralUtil;
import me.t3sl4.updater.utils.SceneUtil;
import me.t3sl4.updater.utils.SystemVariables;

import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;

public class Main extends Application {
    List<Screen> screens = Screen.getScreens();

    public static Screen defaultScreen;

    @Override
    public void start(Stage primaryStage) throws IOException {
        GeneralUtil.prefs = Preferences.userRoot().node("onderGrupUpdater");
        FileUtil.criticalFileSystem();

        if(!System.getProperty("os.name").toLowerCase().contains("win")) {
            if (!GeneralUtil.checkSingleInstance()) {
                System.out.println("Program zaten çalışıyor. Odaklanıyor...");
                Platform.exit();
                return;
            }
        }

        checkVersionFromPrefs();

        Platform.setImplicitExit(false);

        defaultScreen = screens.get(0);
        SceneUtil.openMainScreen(screens.get(0));
    }

    private void checkVersionFromPrefs() {
        String updaterVersionKey = "updater_version";

        String currentVersion = SystemVariables.CURRENT_VERSION;

        String savedUpdaterVersion = GeneralUtil.prefs.get(updaterVersionKey, null);

        if (savedUpdaterVersion == null || !savedUpdaterVersion.equals(currentVersion)) {
            GeneralUtil.prefs.put(updaterVersionKey, currentVersion);
            savedUpdaterVersion = GeneralUtil.prefs.get(updaterVersionKey, null);
        }

        System.out.println("Updater sürümü: " + savedUpdaterVersion);
    }

    public static void main(String[] args) {
        launch(args);
    }
}