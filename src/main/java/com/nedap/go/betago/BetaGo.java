package com.nedap.go.betago;

import com.nedap.go.betago.sequences.CheckersSequence;
import com.nedap.go.server.Logger;
import com.nedap.go.utilities.Board;
import com.nedap.go.utilities.MoveValidator;
import com.nedap.go.utilities.TileColour;
import com.nedap.go.utilities.exceptions.InvalidBoardException;
import com.nedap.go.utilities.exceptions.InvalidMoveException;

import java.util.*;

public class BetaGo {
    private Board board;
    private TileColour colour;
    private Ladder ladder;
    private boolean opponentPassed = false;
    private Sequence sequence;
    private int maxMoveTime;
    private int maxMove;

    public BetaGo(int size, TileColour colour, int maxMoveTime) {
        this.board = new Board(size);
        this.colour = colour;
        this.maxMoveTime = maxMoveTime;
        this.sequence = new CheckersSequence(this.board, this.colour);
    }

    /**
     * Update the Board object with the played move
     * @param move Move object as determined in the protocol
     */
    public void update(String move) {
        String[] tokens = move.split(";");

        if (tokens.length != 2) {
            return;
        }

        int index = Integer.parseInt(tokens[0]);
        TileColour colour = tokens[1].equals("1") ? TileColour.BLACK : TileColour.WHITE;

        opponentPassed = index == -1;

        try {
            board.tryMove(index, colour);
        } catch (InvalidMoveException e) {
            //
        }
    }

    /**
     * Finds and returns the "best possible" move
     * @return one-dimensional index of the suggested move.
     */
    public Integer move() {
        List<Integer> validMoves = new ArrayList<>(this.validMoves());
        Set<Integer> ladderMoves = this.potentialLadderMoves();

        // No valid moves left. Pass by default.
        if(validMoves.size() == 0) {
            return -1;
        }


        // Opponent passed and we are in the lead so we will also pass.
        if (this.opponentPassed) {
            Map<TileColour, Double> score = this.board.scoreProvider().getScore();

            if (score.get(this.colour) > score.get(this.colour.other())) {
                return -1;
            }
        }

        long systemTimeInMilliSeconds = System.currentTimeMillis();
        this.maxMove = validMoves.get((int)(Math.random() * validMoves.size()));

        MoveProvider moveProvider = new MoveProvider(this, validMoves, ladderMoves, this.maxMove, this.colour, this.board);
        moveProvider.start();

        while (moveProvider.isAlive()  && System.currentTimeMillis() - systemTimeInMilliSeconds < this.maxMoveTime) {
            // do nothing
        }

        return this.maxMove;
    }

    /**
     * Update the current best move found by the moveprovider
     * @param nextMove one-dimensional index of the best move for now
     */
    public void updateMaxMove(Integer nextMove) {
        this.maxMove = nextMove;
    }

    /**
     * Returns a list of dead move that should be looked at. Filters out "dead shapes"
     * and checks validity of dead remaining empty tiles.
     * @return List containing the one-dimensional indices of dead valid moves
     */
    private Set<Integer> validMoves() {
        Set<Integer> emptyTiles = new HashSet<>();

        try {
            List<ShapeFilter> filters = ShapeFilterFactory.dead(this.board.size(), this.colour.asChar());
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

    public Set<Integer> potentialLadderMoves () {
        List<ShapeFilter> filters = ShapeFilterFactory.ladder(this.board.size(), this.colour.asChar(), this.colour.other().asChar());
        String boardState = this.board.currentState();

        for (ShapeFilter filter: filters) {
            boardState = filter.filter(boardState);
        }

        Set<Integer> potentialLadders = new HashSet<>();
        for (int i = 0; i < boardState.length(); i ++) {
            if (boardState.charAt(i) == 'L') {
                potentialLadders.add(i);
            }
        }

        return potentialLadders;
    }

}
