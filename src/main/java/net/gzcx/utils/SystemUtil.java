package net.gzcx.utils;

import java.io.File;

public class SystemUtil {
    private static String osName = System.getProperty("os.name");
    public static String configHome = System.getProperty("user.home") + File.separator + ".RgTool";

    public static boolean isMacOs() {
        return osName.contains("Mac");
    }

    public static boolean isWindowsOs() {
        return osName.contains("Windows");
    }

    public static boolean isLinuxOs() {
        return osName.contains("Linux");
    }
}
