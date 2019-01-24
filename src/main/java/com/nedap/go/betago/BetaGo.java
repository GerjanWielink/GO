package com.nedap.go.betago;

import com.nedap.go.utilities.Board;
import com.nedap.go.utilities.MoveValidator;
import com.nedap.go.utilities.TileColour;
import com.nedap.go.utilities.exceptions.InvalidMoveException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BetaGo {
    private Board board;
    private TileColour colour;

    public BetaGo(int size, TileColour colour) {
        this.board = new Board(size);
        this.colour = colour;
    }

    public void update(String move) {
        String[] tokens = move.split(";");

        if (tokens.length != 2) {
            return;
        }

        int index = Integer.parseInt(tokens[0]);
        TileColour colour = tokens[1].equals("1") ? TileColour.BLACK : TileColour.WHITE;

        try {
            board.tryMove(index, colour);
        } catch (InvalidMoveException e) {
            //
        }
    }

    public Integer move() {
        List<Integer> validMoves = new ArrayList<>(this.validMoves());

        // pass if no validmoves left
        if(validMoves.size() == 0) {
            return -1;
        }

        try {
            Thread.sleep(9);
        } catch (InterruptedException e) {
            //
        }

        return validMoves.get((int) (validMoves.size() * Math.random()));
    }

    private Set<Integer> validMoves() {
        Set<Integer> emptyTiles = this.board.extractTilesOfColour(TileColour.EMPTY);
        Set<Integer> validMoves = new HashSet<>(emptyTiles);
        MoveValidator validator = this.board.moveValidator();

        emptyTiles.forEach(tileIndex -> {
            try {
                validator.verify(tileIndex, this.colour);
            } catch (InvalidMoveException e) {
                validMoves.remove(tileIndex);
            }
        });

        return validMoves;
    }

}
