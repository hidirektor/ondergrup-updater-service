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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
                    String launcherVersionKey = "launcher_version";
                    String currentVersion = GeneralUtil.prefs.get(launcherVersionKey, "unknown");

                    String latestVersion = fetchLatestVersionFromGitHub();

                    if (latestVersion != null && !latestVersion.equals(currentVersion)) {
                        handleDownload();
                    } else {
                        runLauncher();
                    }

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

    private String fetchLatestVersionFromGitHub() throws IOException {
        URL url = new URL("https://api.github.com/repos/hidirektor/ondergrup-launcher/releases/latest");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getString("tag_name");
    }

    public void runLauncher() {
        String launcherPath = SystemVariables.launcherPath;

        File launcherFile = new File(launcherPath);

        if (!launcherFile.exists()) {
            System.err.println("Launcher file not found: " + launcherPath);
            handleDownload();
        } else {
            try {
                if (launcherPath.endsWith(".exe")) {
                    // Windows için çalıştırma
                    new ProcessBuilder("cmd.exe", "/c", launcherPath).start();
                } else if (launcherPath.endsWith(".jar")) {
                    // Unix için çalıştırma
                    new ProcessBuilder("java", "-jar", launcherPath).start();
                } else {
                    System.err.println("Unsupported file type for: " + launcherPath);
                }
                GeneralUtil.systemShutdown();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to execute hydraulic file: " + launcherPath);
            }
        }
    }

    public void handleDownload() {
        File selectedDirectory = new File(SystemVariables.mainPath);

        if (!selectedDirectory.exists()) {
            boolean created = selectedDirectory.mkdirs();
            if (!created) {
                System.out.println("İndirme dizini oluşturulamadı: " + SystemVariables.mainPath);
                return;
            }
        }

        File[] matchingFiles = selectedDirectory.listFiles(file -> file.getName().startsWith("windows_Launcher"));

        if (matchingFiles != null && matchingFiles.length > 0) {
            for (File file : matchingFiles) {
                if (file.delete()) {
                    System.out.println("Dosya silindi: " + file.getAbsolutePath());
                } else {
                    System.err.println("Dosya silinemedi: " + file.getAbsolutePath());
                    return;
                }
            }
            System.out.println("Tüm 'windows_Launcher' dosyaları başarıyla silindi.");
        }

        Task<Void> downloadTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    GeneralUtil.downloadLatestVersion(selectedDirectory);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Platform.runLater(() -> {
                    runLauncher();
                });
            }

            @Override
            protected void failed() {
                super.failed();
                Platform.runLater(() -> {
                    System.out.println("Dosya indirilemedi: " + super.getException().getMessage());
                });
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                Platform.runLater(() -> {
                    System.out.println("İndirme iptal edildi.");
                });
            }
        };

        Thread downloadThread = new Thread(downloadTask);
        downloadThread.setDaemon(true);
        downloadThread.start();
    }
}
