package com.nedap.go.utilities;

import com.nedap.go.utilities.exceptions.*;

import java.util.List;

public class MoveValidator {
    private Board board;
    private MoveExecutor executor;

    public MoveValidator(Board board, MoveExecutor executor) {
        this.board = board;
        this.executor = executor;
    }

    public void verify (int index, TileColour colour) throws InvalidMoveException {
        enforceColour(colour);
        enforceBounds(index);
        enforceEmpty(index);
        enforceKo(index, colour);
    }

    public void verifyPass(TileColour colour) throws InvalidMoveException {
        enforceColour(colour);
    }

    private void enforceBounds (int index) throws OutOfBoundsException {
        if (index < 0 || index > board.size() * board.size()) {
            throw new OutOfBoundsException();
        }
    }

    private void enforceColour (TileColour colour) throws BeforeTurnException {
        if (!this.board.turnKeeper().hasTurn(colour)) {
            throw new BeforeTurnException("Player does not have the turn");
        }
    }


    private void enforceEmpty (int index) throws NotEmptyException {
        if(Character.getNumericValue(this.board.currentState().charAt(index)) != TileColour.EMPTY.asNumber()) {
            throw new NotEmptyException("Tile not empty.");
        }
    }

    private void enforceKo (int index, TileColour colour) throws KoException {
        String optimisticNextState = executor.applyOptimistic(index, colour);
        List<String> history = this.board.history();

        for (String state : history) {
            if (state.equals(optimisticNextState)) {
                throw new KoException("Invalid move: Ko rule validated.");
            }
        }
    }
}
