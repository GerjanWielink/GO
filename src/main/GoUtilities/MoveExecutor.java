package GoUtilities;

import GoUtilities.Exceptions.InvalidMoveException;

import java.util.ArrayList;
import java.util.List;

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

        // Remove stones captured by the current player (i.e. stones belonging to the next player) first
        nextBoardState = removeCaptures(
            nextBoardState,
            this.board.turnKeeper().next()
        );

        // Remove stones captured by the next player (i.e. stones belonging to the current player) second
        nextBoardState = removeCaptures(
            nextBoardState,
            this.board.turnKeeper().current()
        );

        return nextBoardState;
    }

    private String removeCaptures(String boardState, TileColour colour) {
        // TODO: this
        List<Integer> tilesOfColour = extractTilesOfColour(boardState, colour);
        tilesOfColour.removeIf(this::isNotCaptured);

        StringBuilder nextBoardStateBuilder = new StringBuilder(boardState);

        tilesOfColour.forEach(index -> {
            nextBoardStateBuilder.setCharAt(index, (char) (TileColour.EMPTY.asNumber() + '0'));
        });

        return nextBoardStateBuilder.toString();
    }

    private List<Integer> extractTilesOfColour(String boardState, TileColour colour) {
        List<Integer> tilesOfColour = new ArrayList<>();

        for (int i = 0; i < boardState.length(); i++) {
            if(boardState.charAt(i) + '0' == colour.asNumber()) {
                tilesOfColour.add(i);
            }
        }

        return tilesOfColour;
    }

    private boolean isNotCaptured(int index) {
        return false;
    }
}
