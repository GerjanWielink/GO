package com.nedap.go.protocol;

import com.nedap.go.utilities.TileColour;

public class ClientCommandBuilder {
    public static final TileColour DEFAULT_TILE_COLOR = TileColour.EMPTY;
    public static final int DEFAULT_BOARD_SIZE = 19;

    public static String handshake(String username) {
        return ClientCommand.HANDSHAKE.toString() + "+" + username;
    }

    public static String setConfig(int gameId, TileColour preferredColour, int boardDimension) {
        return ClientCommand.SET_CONFIG.toString() + "+" + gameId + "+" + preferredColour.asInt() + "+" + boardDimension;
    }

    public static String setConfig(int gameId, int boardDimension) {
        return setConfig(gameId, DEFAULT_TILE_COLOR, boardDimension);
    }

    public static String move(int gameId, String playerName, int tileIndex) {
        return ClientCommand.MOVE.toString() + "+" + gameId + "+" + playerName + "+" + tileIndex;
    }

    public static String pass(int gameId, String playerName) {
        return ClientCommand.PASS.toString() + "+" + gameId + "+" + playerName;
    }

    public static String setRematch(boolean acknowledge) {
        return ClientCommand.SET_REMATCH.toString() + "+" + (acknowledge ? "1" : "0");
    }

    public static String setConfig(int gameId) {
        return setConfig(gameId, TileColour.EMPTY, DEFAULT_BOARD_SIZE);
    }
}
