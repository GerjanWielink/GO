package com.nedap.go.betago;

import com.nedap.go.utilities.Board;
import com.nedap.go.utilities.Group;
import com.nedap.go.utilities.TileColour;
import com.nedap.go.utilities.exceptions.InvalidBoardException;
import com.nedap.go.utilities.exceptions.InvalidMoveException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MoveProvider extends Thread {
    private BetaGo betaGo;
    private List<Integer> validMoves;
    private TileColour colour;
    private Board board;
    private Double maxMoveScore;

    public MoveProvider(BetaGo betaGo, List<Integer> validMoves, Set<Integer> potentialLadderMoves, Integer sequencemove, TileColour colour, Board board) {
        this.betaGo = betaGo;
        this.validMoves = validMoves;
        this.colour = colour;
        this.board = board;

        this.maxMoveScore = scoreMove(sequencemove);
    }


    public void run () {
        List<Integer> validMovesCopy = new ArrayList<>(this.validMoves);

        while (validMovesCopy.size() > 0) {
            Integer potentialMove = this.validMoves.get((int) (this.validMoves.size() * Math.random()));

            Double score = this.scoreMove(potentialMove);
            if (score > this.maxMoveScore) {
                this.maxMoveScore = score;
                this.betaGo.updateMaxMove(potentialMove);
            }

            validMovesCopy.remove(potentialMove);
        }
    }

    private Double scoreMove(Integer index) {
        try {
            Board shallowCopyWithMove = this.shallowCopyWithMove(index);
            Group moveGroup = Group.getGroupFromIndex(shallowCopyWithMove.currentState(), index);

            if (createsDirectThreat(moveGroup)) {
                System.out.println("THIS WORKS SOMETIMES YAY!");
                return -1.0;
            }

            Double scoreDiff = getNetScoreDifference(index, shallowCopyWithMove);

            if (this.colour == TileColour.BLACK) {
                return scoreDiff;
            }

            return scoreDiff + moveGroup.freedoms().size();
        } catch (InvalidBoardException e) {
            return -1.0;
        }
    }

    private Double getNetScoreDifference(Integer index, Board boardWithMove) {
        if (index != -1 && boardWithMove.currentState().charAt(index) != this.colour.asChar()) {
            // implies suicide. We don't bother with that.
            return -1.0;
        }

        Map<TileColour, Double> score = boardWithMove.scoreProvider().getScore();
        double myPoints = score.get(this.colour);
        double opponentPoints = score.get(this.colour.other());

        double moveScore = myPoints - opponentPoints;

        return moveScore;

    }

    private boolean createsDirectThreat (Group moveGroup) {
        return moveGroup.freedoms().size() == 1;
    }

    private Board shallowCopyWithMove(Integer index) throws InvalidBoardException{
        Board shallowBoardCopyWithMove;
        if (index != -1) {
            shallowBoardCopyWithMove = new Board(
                    board.moveExecutor().applyOptimistic(index, this.colour),
                    board.turnKeeper().current()
            );
        } else {
            shallowBoardCopyWithMove = this.board;
        }

        return shallowBoardCopyWithMove;
    }
}
