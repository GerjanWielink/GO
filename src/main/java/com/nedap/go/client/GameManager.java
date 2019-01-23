package com.nedap.go.client;

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

    public GameManager(int size, TileColour colour, String stateString, ClientHandler handler) {
        this.handler = handler;
        this.boardSize = size;
        this.guiConnector = new GuiConnector(size, this::tryMove, this::pass, colour);
        this.readGameState(stateString);
    }

    private void tryMove(int x, int y) {
        this.handler.sendOutBound(ClientCommandBuilder.move(
            this.handler.gameId(),
            this.handler.username(),
            this.indexFromRowCol(x, y)
        ));
    }

    private void pass() {
        this.handler.sendOutBound(ClientCommandBuilder.pass(
                this.handler.gameId(),
                this.handler.username()
        ));
    }

    public void displayMessage(String message) {
        this.guiConnector.displayMessage(message);
    }

    public void update(String move, String stateString) {
        this.readGameState(stateString);
        this.displayMessage(this.formatMoveHumanReadable(move));
    }

    private int indexFromRowCol(int x, int y) {
        return this.boardSize * y + x;
    }

    private Pair<Integer, Integer> indexToRowCol(int index) {
        int x = index % this.boardSize;
        int y = index / this.boardSize;

        return new Pair<>(x, y);
    }


    private void readGameState(String stateString) {
        String[] tokens = stateString.split(";");

        if (tokens.length != 3) {
            // TODO: exception handling
            return;
        }

        this.boardState = tokens[2];
        this.guiConnector.drawBoard(this.boardState);
    }

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
