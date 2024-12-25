package me.t3sl4.updater.utils;

import me.t3sl4.updater.utils.SystemVariables;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {
    public static void criticalFileSystem() throws IOException {
        // İşletim sistemine göre dosya yollarını ayarla
        String userHome = System.getProperty("user.name");
        String os = System.getProperty("os.name").toLowerCase();
        String basePath;
        String programName;

        if (os.contains("win")) {
            basePath = "C:/Users/" + userHome + "/";
            programName = "windows_Launcher.exe";
        } else {
            basePath = "/Users/" + userHome + "/";
            programName = "unix_Launcher.jar";
        }

        // Dosya yollarını belirle
        SystemVariables.mainPath = basePath + "OnderGrup/";
        SystemVariables.launcherPath = SystemVariables.mainPath + programName;

        createDirectory(SystemVariables.mainPath);
    }

    private static void createDirectory(String path) throws IOException {
        Path dirPath = Paths.get(path);
        if (Files.notExists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }

    public static void createFile(String path) throws IOException {
        Path filePath = Paths.get(path);
        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
        }
    }

    public static void fileCopy(String sourcePath, String destPath, boolean isRefresh) throws IOException {
        File destinationFile = new File(destPath);

        if(isRefresh) {
            InputStream resourceAsStream = FileUtil.class.getResourceAsStream(sourcePath);

            if (resourceAsStream == null) {
                throw new FileNotFoundException("Kaynak bulunamadı: " + sourcePath);
            }

            Path destination = Paths.get(destPath);
            Files.copy(resourceAsStream, destination, StandardCopyOption.REPLACE_EXISTING);
            resourceAsStream.close();
        } else {
            if (!destinationFile.exists()) {
                InputStream resourceAsStream = FileUtil.class.getResourceAsStream(sourcePath);

                if (resourceAsStream == null) {
                    throw new FileNotFoundException("Kaynak bulunamadı: " + sourcePath);
                }

                Path destination = Paths.get(destPath);
                Files.copy(resourceAsStream, destination, StandardCopyOption.REPLACE_EXISTING);
                resourceAsStream.close();
            } else {
                System.out.println("File already exists: " + destPath);
            }
        }
    }
}