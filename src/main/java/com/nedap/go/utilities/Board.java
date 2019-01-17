package com.nedap.go.utilities;

import com.nedap.go.utilities.exceptions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Board {
    private int size;
    private String currentState;
    private List<String> history = new ArrayList<>();

    private TurnKeeper turnKeeper;
    private MoveExecutor executor;
    private ScoreProvider scoreProvider;

    /**
     * Construct an empty board of a given size.
     * @param size size of the board
     */
    public Board(int size) {
        this.size = size;
        this.currentState = initialBoard(size);
        this.turnKeeper = new TurnKeeper(2);
        this.executor = new MoveExecutor(this);
        this.scoreProvider = new ScoreProvider(this);
    }

    /**
     * Construct a board with an existing state.
     */
    public Board(String boardState, TileColour currentColour) throws InvalidBoardException {
        int boardSize = (int) Math.sqrt(boardState.length());
        if (boardSize * boardSize != boardState.length()) {
            throw new InvalidBoardException("Board state of length unequal to n^2 provided");
        }

        this.currentState = boardState;
        this.size = boardSize;
        this.turnKeeper = new TurnKeeper(2, currentColour);
        this.executor = new MoveExecutor(this);
        this.scoreProvider = new ScoreProvider(this);
    }

    /**
     *
     * @return
     */
    int size() {
        return this.size;
    }

    /**
     *
     * @return
     */
    String currentState() {
        return this.currentState;
    }

    /**
     *
     * @return
     */
    List<String> history() {
        return this.history;
    }

    public void tryMove(int index, TileColour colour) throws InvalidMoveException {
        this.executor.apply(index, colour);
    }

    /**
     *
     * @param nextBoardState
     */
    public void updateBoard(String nextBoardState) {
        this.history.add(this.currentState);
        this.currentState = nextBoardState;
        turnKeeper.passTurn();
    }

    /**
     *
     * @return
     */
    public TurnKeeper turnKeeper() {
        return this.turnKeeper;
    }

    public void printCurrentState() {
        StringBuilder formattedBoard = new StringBuilder();
        String stateWithTiles = this.currentState.replaceAll("0", "⛶");
        stateWithTiles = stateWithTiles.replaceAll("1", "⚫");
        stateWithTiles = stateWithTiles.replaceAll("2", "⚪");

    for (int i = 0; i < this.size * this.size; i += this.size) {
            formattedBoard.append(stateWithTiles.substring(i, i + this.size) + "\n");
        }

        System.out.print(formattedBoard.toString());
    }

    /**
     *
     * @param size
     * @return
     */
    private String initialBoard(int size) {
        StringBuilder board = new StringBuilder();

        for (int i = 0; i < size * size; i++) {
            board.append(TileColour.EMPTY.asNumber());
        }
        return board.toString();
    }

    public Set<Integer> extractTilesOfColour(String boardState, TileColour colour) {
        Set<Integer> tilesOfColour = new HashSet<>();

        for (int i = 0; i < boardState.length(); i++) {
            if(Character.getNumericValue(boardState.charAt(i)) == colour.asNumber()) {
                tilesOfColour.add(i);
            }
        }

        return tilesOfColour;
    }

    public Set<Integer> extractTilesOfColour(TileColour colour) {
        return this.extractTilesOfColour(this.currentState, colour);
    }

    public ScoreProvider scoreProvider() {
        return this.scoreProvider;
    }

    public Set<Integer> getNeighbourIndices(int index) {
        Set<Integer> neighbours = new HashSet<>();

        // not on top row
        if (index > this.size) {
            neighbours.add(index - this.size);
        }
        // not on bottom row
        if (index < (this.size - 1) * this.size) {
            neighbours.add(index + this.size);
        }
        // not on left edge
        if (index % this.size != 0) {
            neighbours.add(index - 1);
        }
        // not on right edge
        if ((index + 1) % this.size != 0) {
            neighbours.add(index + 1);
        }

        return neighbours;
    }
}