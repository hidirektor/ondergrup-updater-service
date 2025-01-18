package me.t3sl4.updater.utils;

public class SystemVariables {
    public static final String CURRENT_VERSION = "v2.0.2";

    public static final String REPO_OWNER = "hidirektor";
    public static final String HYDRAULIC_REPO_NAME = "hydraulic-tool-desktop";

    public static final String PREF_NODE_NAME = "ondergrup";
    public static final String PREF_UPDATER_KEY = "updater_version";
    public static final String PREF_HYDRAULIC_KEY = "hydraulic_tool";
    public static final String PREF_DEFAULT_DISPLAY = "default_display";

    public static String mainPath;
    public static String hydraulicPath;

    public static String getVersion() {
        return CURRENT_VERSION;
    }
}