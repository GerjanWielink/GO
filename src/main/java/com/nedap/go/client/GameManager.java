package com.nedap.go.client;

import com.nedap.go.betago.BetaGo;
import com.nedap.go.protocol.ClientCommandBuilder;
import com.nedap.go.utilities.TileColour;
import javafx.util.Pair;


public class GameManager {
    private ClientHandler handler;
    private GuiConnector guiConnector;
    private int boardSize;
    private TileColour colour;
    private String boardState;
    private TileColour turn;
    private boolean jeSuisAi;
    private BetaGo betaGo;
    private int maxMoveTime;

    public GameManager(int size, TileColour colour, String stateString, ClientHandler handler, boolean ai, int maxMoveTime, String opponentUsername) {
        this.handler = handler;
        this.boardSize = size;
        this.colour = colour;
        this.guiConnector = new GuiConnector(size, this::tryMove, this::pass, colour, opponentUsername);
        this.readGameState(stateString);
        this.jeSuisAi = ai;
        this.maxMoveTime = maxMoveTime;
        this.betaGo = new BetaGo(this.boardSize, this.colour, maxMoveTime);

        this.updateAi(null);
    }

    /**
     * Sends a MOVE command to the server. If the ai is playing it displays
     * an appropriate error message.
     * @param x Row of the tile.
     * @param y Column of the tile.
     */
    private void tryMove(int x, int y) {
        if (!this.jeSuisAi) {
            this.handler.sendOutBound(ClientCommandBuilder.move(
                this.handler.gameId(),
                this.handler.username(),
                this.indexFromRowCol(x, y)
            ));
        } else {
            this.displayMessage("Computer says no.");
        }
    }

    /**
     * Send a PASS command to the server
     */
    private void pass() {
        this.handler.sendOutBound(ClientCommandBuilder.pass(
            this.handler.gameId(),
            this.handler.username()
        ));
    }

    /**
     * Passes a message to the GUI connector to be displayed on the GUI.
     * @param message Message to be displayed.
     */
    public void displayMessage(String message) {
        this.guiConnector.displayMessage(message);
    }

    /**
     * Updates the game based on the move and boardState received from the server.
     * @param move Move object as defined in the protocol.
     * @param stateString State object as defined in the protocol.
     */
    public void update(String move, String stateString) {
        this.readGameState(stateString);
        this.updateAi(move);
        this.displayMessage(this.formatMoveHumanReadable(move));
    }


    /**
     * Pass the move received from the server to the AI and respond with a move
     * if we have the turn.
     * @param move update the AI state and fetch it's next move.
     * Can be null to trigger the initial AI move.
     */
    private void updateAi(String move) {
        if (move != null) {
            this.betaGo.update(move);
        }

        if (this.turn == this.colour) {
            // Always calculate the next move if we have the turn for the hint indicator.
            int nextAIMove = this.betaGo.move();

            // If there is an AI player send the next move to the server automatically. If not we show the indicator
            if(this.jeSuisAi) {
                this.handler.sendOutBound(ClientCommandBuilder.move(
                        this.handler.gameId(),
                        this.handler.username(),
                        nextAIMove
                ));
            } else {
                this.guiConnector.displayHintIndicator(nextAIMove);
            }
        } else {
            // If we do not have the turn we remove the hint indicator.
            this.guiConnector.removeHintIndicator();
        }
    }


    /**
     * Reset the game state. Used for rematches on the same settings.
     * @param stateString Current state of the game. Should me "000...000" but
     * might be different in future versions with extra features
     */
    public void resetGame(String stateString) {
        this.readGameState(stateString);
        this.betaGo = new BetaGo(this.boardSize, this.colour, this.maxMoveTime);
        this.updateAi(null);
    }


    /**
     * Caculates the index of a tile in the one-dimensional state string
     * from it's row and column
     * @param x Row.
     * @param y Column.
     * @return One-dimensional index
     */
    private int indexFromRowCol(int x, int y) {
        return this.boardSize * y + x;
    }

    /**
     * Calculates the Row and Column index based on the one-dimensional index
     * of a tile.
     * @param index One-dimensional tile index
     * @return Pair containing the coordinates as <Row, Col>.
     */
    private Pair<Integer, Integer> indexToRowCol(int index) {
        int x = index % this.boardSize;
        int y = index / this.boardSize;

        return new Pair<>(x, y);
    }


    /**
     * Takes a game state as sent by the server and updates the game state
     * and GUI accordingly
     * @param stateString state of the board as provided by the server.
     */
    private void readGameState(String stateString) {
        String[] tokens = stateString.split(";");

        if (tokens.length != 3) {
            // TODO: exception handling
            return;
        }

        this.turn = tokens[1].equals("1") ? TileColour.BLACK : TileColour.WHITE;
        this.boardState = tokens[2];
        this.guiConnector.drawBoard(this.boardState);
    }

    /**
     * Formats the move to a string which can be displayed on the GUI
     * @param move Move object as defined in the protocol
     * @return Humans readable string describing the move.
     */
    private String formatMoveHumanReadable(String move) {
        String[] tokens = move.split(";");

        if (tokens.length != 2) {
            return "Unable to read move";
        }

        int index = Integer.parseInt(tokens[0]);
        TileColour colour = tokens[1].equals("1") ? TileColour.BLACK : TileColour.WHITE;

        if (index == -1) {
            return colour.toString() + " passed";
        }

        Pair<Integer, Integer> coordinates = indexToRowCol(index);
        return colour.toString() + " played (" + coordinates.getKey() + "," + coordinates.getValue() + ").";
    }
}
