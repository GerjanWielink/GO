package com.nedap.go.client;

import com.nedap.go.gui.GoGuiIntegrator;
import com.nedap.go.gui.OnClickPassHandler;
import com.nedap.go.gui.OnClickTileHandler;
import com.nedap.go.utilities.Logger;
import com.nedap.go.utilities.TileColour;
import javafx.util.Pair;

public class GuiConnector {
    private GoGuiIntegrator goGui;
    private int boardSize;
    private String currentBoardState;

    public GuiConnector(int boardSize, OnClickTileHandler onClickTileHandler, OnClickPassHandler onClickPassHandler, TileColour colour, String opponentUsername) {
        this.boardSize = boardSize;

        this.goGui = new GoGuiIntegrator(
                boardSize,
                onClickTileHandler,
                onClickPassHandler,
                colour,
                opponentUsername
        );
        this.goGui.startGUI();
        this.goGui.setBoardSize(boardSize);

        this.clearBoard();

        StringBuilder initialBoardBuiler = new StringBuilder();
        for (int i = 0; i < boardSize * boardSize; i++) {
            initialBoardBuiler.append(TileColour.EMPTY.asChar());
        }

        this.currentBoardState = initialBoardBuiler.toString();
    }

    public void drawBoard(String boardState) {
        if (boardState.length() != boardSize * boardSize) {
            Logger.log("NOPE!");
            return;
        }

        for (int i = 0; i < boardState.length(); i++) {
            if (boardState.charAt(i) == this.currentBoardState.charAt(i)) {
                continue;
            }
            Pair<Integer, Integer> coordinates = this.coordinates(i);

            this.goGui.removeStone(coordinates.getKey(), coordinates.getValue());

            if (boardState.charAt(i) != TileColour.EMPTY.asChar()) {
                this.layStone(coordinates, boardState.charAt(i) == TileColour.WHITE.asChar());
            }
        }

        this.currentBoardState = boardState;
    }

    /**
     * Remove the old hint indicator and display the new one.
     * @param index one-dimensional index of the tile.
     */
    public void displayHintIndicator(int index) {
        this.removeHintIndicator();

        Pair<Integer, Integer> coordinates = this.coordinates(index);

        this.goGui.addHintIndicator(coordinates.getKey(), coordinates.getValue());
    }

    /**
     * Clear the hint indicator if present.
     */
    public void removeHintIndicator() {
        this.goGui.removeHintIdicator();
    }

    public void layStone (Pair<Integer, Integer> coordinates, boolean white) {
        this.goGui.addStone(coordinates.getKey(), coordinates.getValue(), white);
    }

    public void clearBoard() {
        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < this.boardSize; j++) {
                this.goGui.removeStone(i, j);
            }
        }
    }

    public void displayMessage(String message) {
        this.goGui.updateTextMessage(message);
    }

    private Pair<Integer, Integer> coordinates(int index) {
        int x = index % this.boardSize;
        int y = index / this.boardSize;

        return new Pair<>(x, y);
    }



}