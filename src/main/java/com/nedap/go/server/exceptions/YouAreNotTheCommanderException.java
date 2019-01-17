package com.nedap.go.server.exceptions;

public class YouAreNotTheCommanderException extends Exception{
    public YouAreNotTheCommanderException() {
        super("This is not da way!");
    }
}
