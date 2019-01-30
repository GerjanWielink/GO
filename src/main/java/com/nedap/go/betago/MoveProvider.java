package com.nedap.go.betago;

import com.nedap.go.utilities.Board;
import com.nedap.go.utilities.Group;
import com.nedap.go.utilities.TileColour;
import com.nedap.go.utilities.exceptions.InvalidBoardException;

import java.util.*;

public class MoveProvider extends Thread {
    private BetaGo betaGo;
    private List<Integer> validMoves;
    private TileColour colour;
    private Board board;
    private Double maxMoveScore;

    private static final int FREEDOM_WEIGHT = 1;
    private static final int SCORE_WEIGHT = 2;

    public MoveProvider(BetaGo betaGo, List<Integer> validMoves, Integer sequencemove, TileColour colour, Board board) {
        this.betaGo = betaGo;
        this.validMoves = validMoves;
        this.colour = colour;
        this.board = board;

        this.maxMoveScore = scoreMove(sequencemove);
    }


    /**
     * Main body of the move scoring Thread. Tries all available move as long as it has time
     * and signals the AI when it finds a better move than the previous.
     */
    public void run () {
        List<Integer> validMovesCopy = new ArrayList<>(this.validMoves);

        while (validMovesCopy.size() > 0 && !Thread.currentThread().isInterrupted()) {
            Long startTime = System.nanoTime();

            Integer potentialMove = this.validMoves.get((int) (this.validMoves.size() * Math.random()));

            Double score = this.scoreMove(potentialMove);

            if (score > this.maxMoveScore) {
                this.maxMoveScore = score;
                this.betaGo.updateMaxMove(potentialMove);
            }

            validMovesCopy.remove(potentialMove);
            System.out.println(((System.nanoTime() - startTime) / 1000000) + " mili seconds to evaluate a move");
        }
    }

    private Double scoreMove(Integer index) {
        if (index == -1) {
            return -10.0;
        }

        Set<Integer> neighbourIndices = this.board.getNeighbourIndices(index);
        Set<Integer> emptyNeighbourIndices = new HashSet<>();
        for(Integer neighbourIndex: neighbourIndices) {
            if (this.board.currentState().charAt(neighbourIndex) == TileColour.EMPTY.asChar()) {
                emptyNeighbourIndices.add(neighbourIndex);
            }
        }

        if (emptyNeighbourIndices.size() == neighbourIndices.size()) {
            return neighbourIndices.size() / 4.0;
        } else {
            try {
                Board shallowCopyWithMove = this.shallowCopyWithMove(index);
                Group moveGroup = Group.getGroupFromIndex(shallowCopyWithMove.currentState(), index);

                if (createsDirectThreat(moveGroup)) {
                    return -1000.0;
                }

                Double scoreDiff = getNetScoreDifference(index, shallowCopyWithMove);

                return (scoreDiff * SCORE_WEIGHT) + (moveGroup.freedoms().size() + evaluateFreedomsTaken(index, shallowCopyWithMove) / 2) * FREEDOM_WEIGHT;
            } catch (InvalidBoardException e) {
                return -1.0;
            }
        }
    }


    private int evaluateFreedomsTaken(Integer index, Board boardWithMove) {
            List<Integer> opponentNeighbourIndices = new ArrayList<>(boardWithMove.getNeighbourIndices(index));
            opponentNeighbourIndices.removeIf(neighbourIndex -> boardWithMove.currentState().charAt(neighbourIndex) != this.colour.other().asChar());

            return opponentNeighbourIndices.size();
    }

    /**
     * Determines the difference in actual area covered by both players after the move
     * @param index index of the move
     * @param boardWithMove Board object with the move implemented
     * @return score gained by the move
     */
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

    /**
     * Group the placed tile belongs to. Checks if this group can be directly caught
     * @param moveGroup Group the tile belongs to.
     * @return if a direct threat is present for the group.
     */
    private boolean createsDirectThreat (Group moveGroup) {
        return moveGroup.freedoms().size() == 1;
    }

    /**
     * Makes a shallow (e.g. without history) copy of the board after the move for scoring purposes
     * @param index index of the move.
     * @return Shallow copy of the board with the intended move played.
     * @throws InvalidBoardException
     */
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
