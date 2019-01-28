package com.nedap.go.betago;

import com.nedap.go.betago.sequences.CheckersSequence;
import com.nedap.go.utilities.Board;
import com.nedap.go.utilities.MoveValidator;
import com.nedap.go.utilities.TileColour;
import com.nedap.go.utilities.exceptions.InvalidBoardException;
import com.nedap.go.utilities.exceptions.InvalidMoveException;

import java.util.*;

public class BetaGo {
    private Board board;
    private TileColour colour;
    private boolean oponnentPassed = false;
    private Sequence sequence;
    private int maxMoveTime;
    private int maxMove;

    public BetaGo(int size, TileColour colour, int maxMoveTime) {
        this.board = new Board(size);
        this.colour = colour;
        this.maxMoveTime = maxMoveTime;
        this.sequence = new CheckersSequence(this.board, this.colour);
    }

    public void update(String move) {
        String[] tokens = move.split(";");

        if (tokens.length != 2) {
            return;
        }

        int index = Integer.parseInt(tokens[0]);
        TileColour colour = tokens[1].equals("1") ? TileColour.BLACK : TileColour.WHITE;

        if (index == -1) {
            oponnentPassed = true;
            return;
        }

        oponnentPassed = false;

        try {
            board.tryMove(index, colour);
        } catch (InvalidMoveException e) {
            //
        }
    }

    public Integer move() {
        List<Integer> validMoves = new ArrayList<>(this.validMoves());

        // No valid moves left. Pass by default.
        if(validMoves.size() == 0) {
            return -1;
        }


        // Opponent passed and we are in the lead so we will also pass.
        if (this.oponnentPassed) {
            Map<TileColour, Double> score = this.board.scoreProvider().getScore();

            if (score.get(this.colour) > score.get(this.colour.other())) {
                return -1;
            }
        }

        long systemTimeInMilliSeconds = System.currentTimeMillis();

        // initiate the nextMove to a random move which we'll try to improve.
        if (this.colour == TileColour.WHITE) {
            this.maxMove = this.sequence.next();
        } else {
            maxMove = validMoves.get((int)(Math.random() * validMoves.size()));
        }


        MoveProvider moveProvider = new MoveProvider(this, validMoves, this.maxMove, this.colour, this.board);
        moveProvider.start();

        while ((moveProvider.isAlive() || System.currentTimeMillis() - systemTimeInMilliSeconds < 500) && System.currentTimeMillis() - systemTimeInMilliSeconds < this.maxMoveTime) {
            // do nothing
        }

        return this.maxMove;
    }

    public void updateMaxMove(Integer nextMove) {
        this.maxMove = nextMove;
    }

    private Set<Integer> validMoves() {
        Set<Integer> emptyTiles = new HashSet<>();

        try {
            List<ShapeFilter> filters = ShapeFilterFactory.all(this.board.size(), this.colour.asChar());
            filters.addAll(ShapeFilterFactory.all(this.board.size(), this.colour.other().asChar()));
            String filteredBoardState = this.board.currentState();


            for (ShapeFilter filter: filters) {
                filteredBoardState = filter.filter(filteredBoardState);
            }

            Board filteredBoard = new Board(filteredBoardState, null);
            emptyTiles = filteredBoard.extractTilesOfColour(TileColour.EMPTY);
        } catch (InvalidBoardException e) {
            this.board.extractTilesOfColour(TileColour.EMPTY);
        }

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
