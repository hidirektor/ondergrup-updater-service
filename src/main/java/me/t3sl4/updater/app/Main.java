package me.t3sl4.updater.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Screen;
import javafx.stage.Stage;
import me.t3sl4.updater.utils.FileUtil;
import me.t3sl4.updater.utils.GeneralUtil;
import me.t3sl4.updater.utils.SceneUtil;
import me.t3sl4.updater.utils.SystemVariables;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;
import java.util.prefs.Preferences;

public class Main extends Application {
    List<Screen> screens = Screen.getScreens();

    public static Screen defaultScreen;

    @Override
    public void start(Stage primaryStage) throws IOException {
        GeneralUtil.prefs = Preferences.userRoot().node("onderGrupUpdater");
        FileUtil.criticalFileSystem();

        checkVersionFromPrefs();

        Platform.setImplicitExit(false);

        defaultScreen = screens.get(0);
        SceneUtil.openMainScreen(screens.get(0));
    }

    private void checkVersionFromPrefs() {
        String updaterVersionKey = "launcher_version";

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