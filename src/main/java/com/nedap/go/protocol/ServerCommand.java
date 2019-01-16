package com.nedap.go.protocol;

public enum ServerCommand {
    ACKNOWLEDGE_HANDSHAKE("ACKNOWLEDGE_HANDSHAKE"),
    REQUEST_CONFIG("REQUEST_CONFIG"),
    ACKNOWLEDGE_CONFIG("ACKNOWLEDGE_CONFIG"),
    ACKNOWLEDGE_MOVE("ACKNOWLEDGE_MOVE"),
    INVALID_MOVE("INVALID_MOVE"),
    UPDATE_STATUS("UPDATE_STATUS");

    private String tag;

    ServerCommand(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString(){
        return this.tag;
    }
}
