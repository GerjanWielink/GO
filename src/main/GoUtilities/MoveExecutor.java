package GoUtilities;

import GoUtilities.Exceptions.InvalidMoveException;

public class MoveExecutor {
    private Board board;
    private MoveValidator validator;

    public MoveExecutor(Board board) {
        this.board = board;
        this.validator = new MoveValidator(this.board, this);
    }

    /**
     * Validates and applies the move to the board. If any rules are violated
     * an exception is thrown.
     */
    public String apply(int index, TileColour colour) throws InvalidMoveException {
        this.validator.verify(index, colour);
        return applyOptimistic(index, colour);
    }

    /**
     * Applies a move without checking any game rules. Assumes these rules are checked elsewhere
     * and enables the checking of rules which need to look at the outcome of the move
     */
    public String applyOptimistic(int index, TileColour colour) {
        StringBuilder nextBoardStateBuilder = new StringBuilder(this.board.currentState());

        nextBoardStateBuilder.setCharAt(index, (char)(colour.asNumber() + '0'));

        String nextBoardState = nextBoardStateBuilder.toString();
        nextBoardState = removeCaptures(
            nextBoardState,
            this.board.turnKeeper().current()
        );

        nextBoardState = removeCaptures(
            nextBoardState,
            this.board.turnKeeper().next()
        );

        return nextBoardState;
    }

    private String removeCaptures(String boardState, TileColour colour) {
        // TODO: this
        return boardState;
    }
}
