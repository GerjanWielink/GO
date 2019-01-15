package GoUtilities;

import GoUtilities.Exceptions.*;

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

    private void enforceBounds (int index) throws OutOfBoundsException {
        if (index < 0 || index > board.size() * board.size()) {
            throw new OutOfBoundsException();
        }
    }

    private void enforceColour (TileColour colour) throws BeforeTurnException {
        if (!this.board.turnKeeper().hasTurn(colour)) {
            throw new BeforeTurnException();
        }
    }


    private void enforceEmpty (int index) throws NotEmptyException {

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