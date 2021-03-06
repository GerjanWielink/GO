package com.nedap.go.utilities;

import com.nedap.go.utilities.exceptions.*;

import javafx.util.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MoveExecutor {
    private Board board;
    private MoveValidator validator;

    public MoveExecutor(Board board) {
        this.board = board;
        this.validator = new MoveValidator(this.board, this);
    }

    /**
     * Validates and applies the move to the board. If any rules are violated
     * an exceptions is thrown.
     */
    public void apply(int index, TileColour colour) throws InvalidMoveException {
        this.validator.verify(index, colour);

        board.updateBoard(applyOptimistic(index, colour));
    }

    /**
     *
     * @param colour player doing the pass
     * @return boolean indicating if a next move is required
     * @throws InvalidMoveException
     */
    public boolean applyPass(TileColour colour) throws  InvalidMoveException {
        this.validator.verifyPass(colour);

        if (board.turnKeeper().passed()){
            // TODO: end the game.
            return false;
        }

        board.turnKeeper().pass();
        return true;
    }

    /**
     * Applies a move without checking any game rules. Assumes these rules are checked elsewhere
     * and enables the checking of rules which need to look at the outcome of the move. Does not actually
     * do anything to the Board state by itself.
     *
     * @param index index of the tile to be changed
     * @param colour colour of the stone to be put on the index
     * @return the boardState after the implementation of the move
     */
    public String applyOptimistic(int index, TileColour colour) {
        if (index == -1) {
            return this.board.currentState();
        }

        StringBuilder nextBoardStateBuilder = new StringBuilder(this.board.currentState());

        nextBoardStateBuilder.setCharAt(index, colour.asChar());



        String nextBoardState = nextBoardStateBuilder.toString();

        // Remove stones captured by the current player (i.e. stones belonging to the next player) first
        nextBoardState = removeCaptures(
            nextBoardState,
            index,
            this.board.turnKeeper().next()
        );


        // Remove stones captured by the next player (i.e. stones belonging to the current player) second
        nextBoardState = removeCaptures(
            nextBoardState,
            index,
            this.board.turnKeeper().current()
        );

        return nextBoardState;
    }

    public MoveValidator moveValidator() {
        return this.validator;
    }

    /**
     * @param boardState previous boardState
     * @param colour colour for which captures should be removed
     * @return string containing previous board with the respective captures removed
     */
    public String removeCaptures(String boardState, Integer index, TileColour colour) {
        // A move can only affect groups which are it's direct neighbours so we
        // only need to check these
        Set<Integer> tileNeighbours = this.board.getNeighbourIndices(index);
        tileNeighbours.add(index);

        // First we want to check
        Set<Integer> tilesOfColourUnchecked = new HashSet<>();
        tileNeighbours.forEach(neighbour -> {
            if (boardState.charAt(neighbour) == colour.asChar()) {
                tilesOfColourUnchecked.add(neighbour);
            }
        });

        Set<Integer> tilesToBeRemoved = new HashSet<>();

        while (tilesOfColourUnchecked.size() > 0) {
            Integer currIdx = (int) tilesOfColourUnchecked.toArray()[0];

            Group groupForIndex = Group.getGroupFromIndex(
                    boardState,
                    currIdx
            );

            if (groupForIndex.isCaptured()) {
                tilesToBeRemoved.addAll(groupForIndex.group());
            }

            tilesOfColourUnchecked.removeAll(groupForIndex.group());
        }

        StringBuilder nextBoardStateBuilder = new StringBuilder(boardState);
        for (Integer tileIndex : tilesToBeRemoved) {
            nextBoardStateBuilder.setCharAt(
                    tileIndex,
                   TileColour.EMPTY.asChar()
            );
        }

        return nextBoardStateBuilder.toString();
    }

    public Pair<Boolean, Set<Integer>> checkGroupFromTile(
            String boardState,
            TileColour colour,
            int index,
            Set<Integer> group
    ) {
        Set<Integer> captureGroup = group != null ? group : new HashSet<>(Arrays.asList(index));
        Set<Integer> neighbours = this.board.getNeighbourIndices(index);
        boolean groupFree = false;


        for (int neighbourIndex: neighbours) {
            // already considered
            if (captureGroup.contains(neighbourIndex)) {
                continue;
            }
            // neighbour is empty
            if (boardState.charAt(neighbourIndex) == TileColour.EMPTY.asChar()) {
                groupFree = true;
                continue;
            }
            // neighbour is of same colour
            if (boardState.charAt(neighbourIndex) == colour.asChar()) {
                captureGroup.add(neighbourIndex);
                Pair<Boolean, Set<Integer>> neighbourResult = checkGroupFromTile(boardState, colour, neighbourIndex, captureGroup);

                captureGroup.addAll(neighbourResult.getValue());
                groupFree = groupFree || neighbourResult.getKey();
            }
            // neighbour is of other colour, do nothing
        }

        return new Pair<>(groupFree, captureGroup);
    }
}
