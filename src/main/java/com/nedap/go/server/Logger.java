package com.nedap.go.server;

import java.util.Date;

public class Logger {
    public static void log (String string) {
        System.out.println(timeStamp(string));
    }

    public static void error (String message) {
        Logger.log((char)27 + "[31m" + message);
    }

    private static String timeStamp (String string) {
        return "[" + new Date() + "]: " + string;
    }
}
