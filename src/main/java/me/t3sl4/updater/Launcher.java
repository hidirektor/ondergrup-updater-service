package me.t3sl4.updater;

import me.t3sl4.updater.app.Main;

public class Launcher {
    public static void main(String[] args) {
        System.setProperty("java.util.logging.level", "WARNING");

        Main.main(args);
    }
}