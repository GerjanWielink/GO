package com.nedap.go.server;

import java.util.Date;

public class Logger {
    public static void log (String string) {
        System.out.println(timeStamp(string));
    }

    private static String timeStamp (String string) {
        return "[" + new Date() + "]: " + string;
    }
}
