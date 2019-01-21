package com.nedap.go.client;

import com.nedap.go.protocol.ClientCommandBuilder;
import com.nedap.go.utilities.TileColour;


public class GameHandler {
    ClientHandler handler;
    GuiConnector guiConnector;
    int boardSize;
    TileColour colour;
    String boardState;
    TileColour turn;

    public GameHandler(int size, TileColour colour, String stateString, ClientHandler handler) {
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
    }

    private int indexFromRowCol(int x, int y) {
        return this.boardSize * y + x;
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
}
