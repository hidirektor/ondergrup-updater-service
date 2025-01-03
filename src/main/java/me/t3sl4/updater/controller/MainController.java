package me.t3sl4.updater.controller;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import me.t3sl4.updater.utils.GeneralUtil;
import me.t3sl4.updater.utils.SystemVariables;
import me.t3sl4.util.version.VersionUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private Stage currentStage;

    @FXML
    private ImageView mainLogo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            currentStage = (Stage) mainLogo.getScene().getWindow();

            startBlinkingEffect();
        });

        Task<Void> updateCheckTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    String localVersion = VersionUtil.getLocalVersion(SystemVariables.PREF_NODE_NAME, SystemVariables.PREF_LAUNCHER_KEY);
                    String latestVersion = VersionUtil.getLatestVersion(SystemVariables.REPO_OWNER, SystemVariables.LAUNCHER_REPO_NAME);

                    if (localVersion != null && latestVersion != null && !localVersion.equals(latestVersion)) {
                        handleDownload();
                    }
                    runLauncher();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                return null;
            }
        };

        Thread updateCheckThread = new Thread(updateCheckTask);
        updateCheckThread.setDaemon(true);
        updateCheckThread.start();
    }

    private void startBlinkingEffect() {
        if (mainLogo != null) {
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), mainLogo);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
            fadeTransition.setCycleCount(FadeTransition.INDEFINITE);
            fadeTransition.setAutoReverse(true);
            fadeTransition.play();
        } else {
            System.err.println("mainLogo is null.");
        }
    }

    public void runLauncher() {
        String launcherPath = SystemVariables.launcherPath;

        File launcherFile = new File(launcherPath);

        if(!launcherFile.exists()) {
            System.out.println("Launcher file cant find. Downloading started....");
            handleDownload();
            return;
        }

        new Thread(() -> {
            try {
                if (launcherPath.endsWith(".exe")) {
                    new ProcessBuilder("cmd.exe", "/c", launcherPath).start();
                } else if (launcherPath.endsWith(".jar")) {
                    new ProcessBuilder("java", "-jar", launcherPath).start();
                } else {
                    System.err.println("Unsupported file type for: " + launcherPath);
                    return;
                }

                Thread.sleep(1000);
                Platform.runLater(() -> GeneralUtil.systemShutdown());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                System.err.println("Failed to execute launcher file: " + launcherPath);
            }
        }).start();
    }

    private void handleDownload() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        String launcherFileName;

        if (os.contains("win")) {
            launcherFileName = "windows_Launcher.exe";
        } else if (os.contains("mac")) {
            launcherFileName = "mac_Launcher.jar";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            launcherFileName = "unix_Launcher.jar";
        } else {
            throw new UnsupportedOperationException("Bu işletim sistemi desteklenmiyor: " + os);
        }
        VersionUtil.downloadLatest(SystemVariables.REPO_OWNER, SystemVariables.LAUNCHER_REPO_NAME, SystemVariables.mainPath, launcherFileName);
    }
}
