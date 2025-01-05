package me.t3sl4.updater.utils;

public class SystemVariables {
    public static final String CURRENT_VERSION = "v1.1.6";

    public static final String REPO_OWNER = "hidirektor";
    public static final String LAUNCHER_REPO_NAME = "ondergrup-launcher";

    public static final String PREF_NODE_NAME = "canicula";
    public static final String PREF_UPDATER_KEY = "updater_version";
    public static final String PREF_LAUNCHER_KEY = "launcher_version";
    public static final String PREF_DEFAULT_DISPLAY = "default_display";

    public static String mainPath;
    public static String launcherPath;

    public static String getVersion() {
        return CURRENT_VERSION;
    }
}