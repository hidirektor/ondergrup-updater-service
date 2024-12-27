package me.t3sl4.updater.utils;

import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.prefs.Preferences;

public class GeneralUtil {
    public static final String PREFERENCE_KEY = "defaultMonitor";
    public static Preferences prefs;

    public static boolean netIsAvailable() {
        try {
            final URL url = new URL("http://www.google.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }

    private static String getMonitorBrand(int index) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = ge.getScreenDevices();

        if (index < devices.length) {
            return devices[index].getIDstring();
        }

        return "Unknown Monitor";
    }

    private static void saveSelectedMonitor(String monitor) {
        prefs.put(PREFERENCE_KEY, monitor);
    }

    public static String checkDefaultMonitor() {
        return GeneralUtil.prefs.get(PREFERENCE_KEY, null);
    }

    private static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Dosya silindi: " + filePath);
            } else {
                System.err.println("Dosya silinemedi: " + filePath);
            }
        } else {
            System.out.println("Dosya bulunamadı: " + filePath);
        }
    }

    private static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        deleteFile(file.getAbsolutePath());
                    }
                }
            }
            if (directory.delete()) {
                System.out.println("Dizin silindi: " + directory.getAbsolutePath());
            } else {
                System.err.println("Dizin silinemedi: " + directory.getAbsolutePath());
            }
        } else {
            System.out.println("Dizin bulunamadı: " + directory.getAbsolutePath());
        }
    }

    public static void openFile(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Dosya bulunamadı: " + filePath);
            return;
        }

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(file);
                System.out.println("Dosya başarıyla açıldı: " + filePath);
            } catch (IOException e) {
                System.out.println("Dosya açılamadı: " + e.getMessage());
            }
        } else {
            System.out.println("Bu platform masaüstü fonksiyonlarını desteklemiyor.");
        }
    }

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

    public static void openFolder(String path) {
        try {
            // Dosya nesnesi oluştur
            File folder = new File(path);

            // Klasörün varlığını kontrol et
            if (!folder.exists()) {
                System.err.println("Error: Path does not exist: " + path);
                return;
            }

            // Desktop API ile klasörü aç
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(folder);
                System.out.println("Folder opened: " + path);
            } else {
                System.err.println("Error: Desktop API is not supported on this system.");
            }
        } catch (IOException e) {
            System.err.println("Error opening folder: " + path);
            e.printStackTrace();
        }
    }

    public static void openURL(String url) {
        try {
            // URI oluştur
            URI uri = new URI(url);

            // Desktop API ile URL'yi aç
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(uri);
                    System.out.println("URL opened: " + url);
                } else {
                    System.err.println("Error: BROWSE action is not supported on this system.");
                }
            } else {
                System.err.println("Error: Desktop API is not supported on this system.");
            }
        } catch (URISyntaxException e) {
            System.err.println("Error: Invalid URL syntax: " + url);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error opening URL: " + url);
            e.printStackTrace();
        }
    }

    public static void downloadLatestVersion(File selectedDirectory) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        String downloadURL = getDownloadURLForOS(os);

        if (downloadURL == null) {
            System.out.println("Uygun sürüm bulunamadı.");
            return;
        }

        File downloadFile = new File(selectedDirectory.getAbsolutePath() + "/" + getFileNameFromURL(downloadURL));
        try (BufferedInputStream in = new BufferedInputStream(new URL(downloadURL).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(downloadFile)) {

            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;
            long fileSize = new URL(downloadURL).openConnection().getContentLengthLong();

            while ((bytesRead = in.read(dataBuffer, 0, dataBuffer.length)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }

            System.out.println("Dosya başarıyla indirildi: " + downloadFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static String getDownloadURLForOS(String os) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(SystemVariables.LAUNCHER_URL).openConnection();
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        if (connection.getResponseCode() == 200) {
            String jsonResponse = new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A").next();
            JSONObject releaseData = new JSONObject(jsonResponse);
            JSONArray assets = releaseData.getJSONArray("assets");

            for (int i = 0; i < assets.length(); i++) {
                JSONObject asset = assets.getJSONObject(i);
                String assetName = asset.getString("name");

                if (os.contains("win") && assetName.contains("windows")) {
                    return asset.getString("browser_download_url");
                } else if (os.contains("mac") && assetName.contains("macOS")) {
                    return asset.getString("browser_download_url");
                } else if ((os.contains("nix") || os.contains("nux")) && assetName.contains("linux")) {
                    return asset.getString("browser_download_url");
                }
            }
        } else {
            System.out.println("GitHub API'ye erişilemedi: " + connection.getResponseMessage());
        }

        return null;
    }

    private static String getFileNameFromURL(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
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