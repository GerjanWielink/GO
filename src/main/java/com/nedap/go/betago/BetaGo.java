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
        // No valid moves left. Pass by default.
        if(validMoves.size() == 0) {
            return -1;
        }

        Map<TileColour, Double> currentScore = this.board.scoreProvider().getScore();
        boolean ahead = currentScore.get(this.colour) > currentScore.get(this.colour.other());

        // Opponent passed and we are in the lead so we will also pass.
        if (this.opponentPassed && ahead) {
            return -1;
        }

        // only consider passing if we are ahead
        this.maxMove = ahead ? -1 : validMoves.get((int) (Math.random() * validMoves.size()));

        MoveProvider moveProvider = new MoveProvider(this, validMoves, this.maxMove, this.colour, this.board);

        long systemTimeInMilliSeconds = System.currentTimeMillis();

        moveProvider.start();

        while ((moveProvider.isAlive()  && System.currentTimeMillis() - systemTimeInMilliSeconds < this.maxMoveTime) || System.currentTimeMillis() - systemTimeInMilliSeconds < 200) {
            try {
                Thread.sleep(this.maxMoveTime / 20);
            } catch (InterruptedException e) {
                //
            }
        }
        moveProvider.interrupt();

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
          Set<Integer>emptyTiles = this.board.extractTilesOfColour(TileColour.EMPTY);

          try {
              List<ShapeFilter> filters = ShapeFilterFactory.dead(this.board.size(), this.colour.asChar());
              filters.addAll(ShapeFilterFactory.dead(this.board.size(), this.colour.other().asChar()));
              String filteredBoardState = this.board.currentState();


              for (ShapeFilter filter: filters) {
                  filteredBoardState = filter.filter(filteredBoardState);
              }

              System.out.println(filteredBoardState);

              Board filteredBoard = new Board(filteredBoardState, null);
              emptyTiles = filteredBoard.extractTilesOfColour(TileColour.EMPTY);
          } catch (InvalidBoardException e) {
              //
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
