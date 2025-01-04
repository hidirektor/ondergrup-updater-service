package me.t3sl4.updater.utils;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GeneralUtil {
    public static void systemShutdown() {
        if(!System.getProperty("os.name").toLowerCase().contains("win")) {
            try {
                Path lockFilePath = Path.of(System.getProperty("user.home"), ".onder_grup_updater.pid");
                Files.deleteIfExists(lockFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Platform.exit();
        System.exit(0);
    }

    public static boolean checkSingleInstance() {
        String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        File lockFile = new File(System.getProperty("user.home"), ".onder_grup_updater.pid");

        try {
            if (lockFile.exists()) {
                List<String> lines = Files.readAllLines(lockFile.toPath());
                if (!lines.isEmpty()) {
                    String existingPid = lines.get(0);
                    if (isProcessRunning(existingPid)) {
                        return false;
                    }
                }
            }

            Files.write(lockFile.toPath(), pid.getBytes());
            lockFile.deleteOnExit();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isProcessRunning(String pid) {
        try {
            Process process = Runtime.getRuntime().exec("tasklist");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(pid)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}