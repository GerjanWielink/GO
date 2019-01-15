package GoProtocol;

import GoUtilities.TileColour;

import static main.GoProtocol.ClientCommand.*;

public class ClientCommandBuilder {
    public static final TileColour DEFAULT_TILE_COLOR = TileColour.EMPTY;
    public static final int DEFAULT_BOARD_SIZE = 19;

    public static String handshake(String username) {
        return HANDSHAKE.toString() + "+" + username;
    }

    public static String setConfig(int gameId, TileColour preferredColour, int boardDimension) {
        return SET_CONFIG.toString() + "+" + gameId + "+" + preferredColour.asNumber() + "+" + boardDimension;
    }

    public static String setConfig(int gameId, int boardDimension) {
        return setConfig(gameId, DEFAULT_TILE_COLOR, boardDimension);
    }

    public static String move(int gameId, String playerName, int tileIndex) {
        return MOVE.toString() + "+" + gameId + "+" + playerName + "+" + tileIndex;
    }

    public static String pass(int gameId, String playerName) {
        return PASS.toString() + "+" + gameId + "+" + playerName;
    }

    public static String setConfig(int gameId) {
        return setConfig(gameId, TileColour.EMPTY, DEFAULT_BOARD_SIZE);
    }
}
