package com.nedap.go.utilities;

import com.nedap.go.server.Logger;
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
        enforceBounds(index);
        enforceColour(colour);
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


    private void enforceEmpty (int index) throws InvalidMoveException {
        try {
        if(this.board.currentState().charAt(index) != TileColour.EMPTY.asChar()) {
            throw new NotEmptyException("Tile not empty.");
        }} catch (StringIndexOutOfBoundsException e) {
            throw new OutOfBoundsException();
        }
    }

    private void enforceKo (int index, TileColour colour) throws KoException {
        if (index == -1) {
            return;
        }

        String optimisticNextState = executor.applyOptimistic(index, colour);
        List<String> history = this.board.history();
        history.add(this.board.currentState());

        for (String state : history) {
            if (state.equals(optimisticNextState)) {
                throw new KoException("Invalid move: Ko rule validated.");
            }
        }
    }
}
