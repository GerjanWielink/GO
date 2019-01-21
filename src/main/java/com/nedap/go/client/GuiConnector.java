package com.nedap.go.client;

import com.nedap.go.gui.GoGuiIntegrator;
import com.nedap.go.gui.OnClickHandler;
import com.nedap.go.server.Logger;
import com.nedap.go.utilities.TileColour;
import javafx.util.Pair;

public class GuiConnector {
    private GoGuiIntegrator goGui;
    private int boardSize;

    public GuiConnector(int boardSize, OnClickHandler onClickHandler, TileColour colour) {
        this.boardSize = boardSize;

        this.goGui = new GoGuiIntegrator(true, true, boardSize, onClickHandler, colour);
        this.goGui.startGUI();
        this.goGui.setBoardSize(boardSize);
        this.clearBoard();
    }

    // TODO: difference detection maybe
    public void drawBoard(String boardState) {
        Logger.log(boardState);

        if (boardState.length() != boardSize * boardSize) {
            Logger.log("NOPE!");
            return;
        }

        this.clearBoard();

        for (int i = 0; i < boardState.length(); i++){
            char tile = boardState.charAt(i);
            if (tile == '0') {
                continue;
            }
            if (tile == '1') {
                this.layStone(this.coordinates(i), false);
                continue;
            }
            this.layStone(this.coordinates(i), true);
        }
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

    private Pair<Integer, Integer> coordinates(int index) {
        int x = index % this.boardSize;
        int y = index / this.boardSize;

        return new Pair<>(x, y);
    }



}