package com.moxun.s2v.utils;

import com.intellij.notification.*;

/**
 * logger
 * Created by moxun on 15/11/27.
 */
public class Logger {
    public static String NAME;
    private static int LEVEL = 0;

    public static final int DEBUG = 3;
    public static final int INFO = 2;
    public static final int WARN = 1;
    public static final int ERROR = 0;

    public static void init(String name, int level) {
        NAME = name;
        LEVEL = level;
        NotificationsConfiguration.getNotificationsConfiguration().register(NAME, NotificationDisplayType.NONE);
    }

    public static boolean loggable(int level) {
        return level >= LEVEL;
    }

    public static void debug(String text) {
        if (LEVEL >= DEBUG) {
            Notifications.Bus.notify(
                    new Notification(NAME, NAME + " [DEBUG]", redirect(text), NotificationType.INFORMATION));
        }
    }

    public static void info(String text) {
        if (LEVEL > INFO) {
            Notifications.Bus.notify(
                    new Notification(NAME, NAME + " [INFO]", redirect(text), NotificationType.INFORMATION));
        }
    }

    public static void warn(String text) {
        if (LEVEL > WARN) {
            Notifications.Bus.notify(
                    new Notification(NAME, NAME + " [WARN]", redirect(text), NotificationType.WARNING));
        }
    }

    public static void error(String text) {
        if (LEVEL > ERROR) {
            Notifications.Bus.notify(
                    new Notification(NAME, NAME + " [ERROR]", redirect(text), NotificationType.ERROR));
        }
    }

    private static String redirect(String text) {
        if (LEVEL == DEBUG) {
            StackTraceElement ste = new Throwable().getStackTrace()[2];
            String prefix = ste.getFileName();
            int lineNum = ste.getLineNumber();
            System.err.println("D/svgtoandroid: (" + prefix + ":" + lineNum + ") " + text);
        }
        return text;
    }
}
