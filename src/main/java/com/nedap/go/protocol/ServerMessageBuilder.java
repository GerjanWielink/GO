package com.nedap.go.protocol;

import static com.nedap.go.protocol.ClientCommand.SET_CONFIG;
import static com.nedap.go.protocol.ServerCommand.*;

public class ServerMessageBuilder {

    public static String acknowledgeHandshake(int gameId, boolean isLeader) {
        return ACKNOWLEDGE_HANDSHAKE.toString() + "+" + gameId + "+" + (isLeader ? "1" : "0");
    }

    public static String requestConfig() {
        String message = "Please provide the required configuration as " + SET_CONFIG + "+$GAME_ID+$PREFERRED_COLOR+$BOARD_SIZE";
        return REQUEST_CONFIG.toString() + "+" + message;
    }
}
