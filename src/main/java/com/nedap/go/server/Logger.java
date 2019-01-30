package com.nedap.go.server;

import java.util.Date;

public class Logger {
    public static final boolean ENABLED = false;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_MAGENTA = "\u001b[35m";

    public static void log (String string) {
        System.out.println(timeStamp(string));
    }

    public static void error (String message) {
        if(ENABLED) {
            System.out.println(ANSI_RED + timeStamp(message) + ANSI_RESET);
        }
    }

    public static void inbound(String message) {
        if(ENABLED) {
            System.out.println(ANSI_CYAN + timeStamp(message) + ANSI_RESET);
        }
    }

    public static void outbound(String message) {
        if(ENABLED) {
            System.out.println(ANSI_MAGENTA + timeStamp(message) + ANSI_RESET);
        }
    }


    private static String timeStamp (String string) {
        return "[" + new Date() + "]: " + string;
    }
}
