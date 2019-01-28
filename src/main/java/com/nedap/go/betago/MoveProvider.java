package com.nedap.go.betago;

import com.nedap.go.server.Logger;
import com.nedap.go.utilities.Board;
import com.nedap.go.utilities.TileColour;
import com.nedap.go.utilities.exceptions.InvalidBoardException;

import java.util.List;

public class MoveProvider extends Thread {
    private BetaGo betaGo;
    private List<Integer> validMoves;
    private TileColour colour;
    private Board board;
    private Double maxPoints;

    public MoveProvider(BetaGo betaGo, List<Integer> validMoves, Integer sequencemove, TileColour colour, Board board) {
        this.betaGo = betaGo;
        this.validMoves = validMoves;
        this.colour = colour;
        this.board = board;

        scoreMove(sequencemove);
    }

    /**
     *
     */
    public void run () {
        // TODO: Dead shapes filters.

        while (this.validMoves.size() > 0) {
            Integer potentialMove = this.validMoves.get((int) (this.validMoves.size() * Math.random()));

            this.scoreMove(potentialMove);

            this.validMoves.remove(potentialMove);
        }
    }

    private void scoreMove(Integer index) {
        try {
            Board shallowBoardCopyWithMove;

            if (index != -1) {
                shallowBoardCopyWithMove = new Board(
                        this.board.moveExecutor().applyOptimistic(index, this.colour),
                        this.board.turnKeeper().current()
                );
            } else {
                shallowBoardCopyWithMove = this.board;
            }



            if (index != -1 && shallowBoardCopyWithMove.currentState().charAt(index) != this.colour.asChar()) {
                // implies suicide. We don't bother with that.
                return;
            }

            double points =  (shallowBoardCopyWithMove.scoreProvider().getScore()).get(this.colour);

            if (this.maxPoints == null || points > this.maxPoints) {
                this.maxPoints = points;
                this.betaGo.updateMaxMove(index);
            }

        } catch (InvalidBoardException e) {
            // should not happen. If it does we'll just ignore it and return the random move I guess.
        }
    }
}
