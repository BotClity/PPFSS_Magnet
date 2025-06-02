package com.ppfss.magnet.utils;

import com.ppfss.magnet.PPFSS_Magnet;
import lombok.Getter;
import org.bukkit.configuration.Configuration;

import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class LogUtils {
    private static final boolean ANSI_ENABLED = isAnsiEnabled();
    private static final boolean DEBUG_ENABLED = isDebugEnabled();
    @Getter
    private static final boolean CYRILLIC_ENABLED = isCyrillicEnabled();
    private static final Logger LOGGER = PPFSS_Magnet.getPlugin(PPFSS_Magnet.class).getLogger();

    private static boolean isAnsiEnabled() {
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");
        boolean hasTerm = System.getenv("TERM") != null;
        boolean hasConsole = System.console() != null;
        return !isWindows || hasTerm || hasConsole;
    }

    public static void info(String msg, Object... args) {
        log(Level.INFO, msg, ColorCode.WHITE, args);
    }

    public static void warn(String msg, Object... args) {
        log(Level.WARNING, msg, ColorCode.YELLOW, args);
    }

    public static void error(String msg, Object... args) {
        log(Level.SEVERE, msg, ColorCode.RED, args);
    }

    public static void debug(String msg, Object... args) {
        if (isDebugEnabled()) {
            log(Level.FINE, msg, ColorCode.CYAN, args);
        }
    }

    public static void log(Level level, String msg, ColorCode color, Object... args) {
        String formattedMessage = formatMessage(msg, args);
        String output = ANSI_ENABLED
                ? color.getAnsiCode() +  formattedMessage + ColorCode.RESET.getAnsiCode()
                : formattedMessage;
        LOGGER.log(level, output);
    }

    private static String formatMessage(String message, Object... args) {
        if (message == null || args == null || args.length == 0) {
            return message != null ? message : "";
        }

        StringBuilder result = new StringBuilder();
        int argIndex = 0;
        int start = 0;
        int openBrace;

        while ((openBrace = message.indexOf('{', start)) != -1 && argIndex < args.length) {
            if (openBrace + 1 < message.length() && message.charAt(openBrace + 1) == '}') {
                result.append(message, start, openBrace);
                result.append(args[argIndex] != null ? args[argIndex].toString() : "null");
                start = openBrace + 2;
                argIndex++;
            } else {
                result.append(message, start, openBrace + 1);
                start = openBrace + 1;
            }
        }

        result.append(message.substring(start));
        return result.toString();
    }

    private static boolean isDebugEnabled() {
        Configuration config = PPFSS_Magnet.getPlugin(PPFSS_Magnet.class).getConfig();
        return config.getBoolean("debug", false);
    }

    private static boolean isCyrillicEnabled() {
        boolean isCyrillicConsoleSupported;

        String os = System.getProperty("os.name").toLowerCase();
        String jnuEncoding = System.getProperty("sun.jnu.encoding", "unknown");
        String lang = System.getenv("LANG");

        if (os.contains("win")) {
            isCyrillicConsoleSupported = true;
        } else {
            isCyrillicConsoleSupported = true;
            if (lang != null && !lang.toLowerCase().contains("utf-8")) {
                isCyrillicConsoleSupported = jnuEncoding.equalsIgnoreCase("UTF-8");
            }
        }

        return isCyrillicConsoleSupported;
    }
}