package me.t3sl4.updater.utils;

public class SystemVariables {
    public static final String CURRENT_VERSION = "v1.0.4";

    public static String LAUNCHER_URL = "https://api.github.com/repos/hidirektor/ondergrup-launcher/releases/latest\n";

    public static String mainPath;
    public static String launcherPath;

    public static String getVersion() {
        return CURRENT_VERSION;
    }
}