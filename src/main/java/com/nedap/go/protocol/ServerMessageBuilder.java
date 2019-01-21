package com.nedap.go.protocol;

import com.nedap.go.server.GameInstance;
import com.nedap.go.utilities.TileColour;

import java.util.Map;

import static com.nedap.go.protocol.ClientCommand.SET_CONFIG;
import static com.nedap.go.protocol.ServerCommand.*;

public class ServerMessageBuilder {

    /**
     * @param gameId game id
     * @param isLeader player is game leader
     * @return "ACKNOWLEDGE_HANDSHAKE+$GAME_ID+$ISLEADER"
     */
    public static String acknowledgeHandshake(int gameId, boolean isLeader) {
        return ACKNOWLEDGE_HANDSHAKE.toString() + "+" + gameId + "+" + (isLeader ? "1" : "0");
    }

    /**
     * @return "REQUEST_CONFIG+$MESSAGE"
     */
    public static String requestConfig() {
        String message = "Please provide the required configuration as " + SET_CONFIG + "+$GAME_ID+$PREFERRED_COLOR+$BOARD_SIZE";
        return REQUEST_CONFIG.toString() + "+" + message;
    }

    /**
     * @return ACKNOWLEDGE_CONFIG+$PLAYER_NAME+$COLOR+$SIZE+GAME_STATE
     */
    public static String acknowledgeConfig(String playerName, TileColour colour, GameInstance gameInstance) {
        return ACKNOWLEDGE_CONFIG.toString() +
                "+" + playerName +
                "+" + colour.asNumber() + "+" +
                gameInstance.board().size() + "+" +
                gameStateStringFromGameInstance(gameInstance);
    }

    public static String invalidMove(String message) {
        return INVALID_MOVE + "+" +  message;
    }

    public static String unkownCommand(String message) {
        return "UNKNOWN_COMMAND+" + message;
    }

    /**
     * @return ACKNOWLEDGE_MOVE+$GAME_ID+$MOVE+$GAME_STATE
     */
    public static String acknowledgeMove(int gameId, int index, TileColour colour, GameInstance gameInstance) {
        return ACKNOWLEDGE_MOVE.toString() +
                "+" + gameId +
                "+" + moveFromIndexAndColour(index, colour) +
                "+" + gameStateStringFromGameInstance(gameInstance);
    }

    /**
     * @return GAME_FINISHED+$GAME_ID+$WINNER+$SCORE+$MESSAGE
     */
    public static String gameFinished(int gameId, String winner, Map<TileColour, Double> score, String message) {
        return GAME_FINISHED.toString() +
                "+" + gameId +
                "+" + winner +
                "+" + scoreFromMap(score) +
                "+" + message;
    }

    private static String gameStateStringFromGameInstance(GameInstance gameInstance) {
        return (gameInstance.gameState().toString() + ";") +
                gameInstance.board().turnKeeper().current().asNumber() + ";" +
                gameInstance.board().currentState();
    }

    private static String moveFromIndexAndColour(int index, TileColour colour) {
        return index + ";" + colour.asNumber();
    }

    private static String scoreFromMap(Map<TileColour, Double> score) {
        return TileColour.BLACK.asNumber() +
                ";" + score.get(TileColour.BLACK) +
                ";" + TileColour.WHITE.asNumber() +
                ";" + score.get(TileColour.WHITE);
    }
}
