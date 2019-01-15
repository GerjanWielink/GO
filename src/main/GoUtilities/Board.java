package GoUtilities;

import GoUtilities.Exceptions.InvalidBoardException;
import GoUtilities.Exceptions.InvalidMoveException;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private int size;
    private TurnKeeper turnKeeper;
    private String currentState;
    private List<String> history = new ArrayList<>();
    private MoveExecutor executor;

    /**
     * Construct an empty board of a given size.
     * @param size size of the board
     */
    public Board(int size) {
        this.size = size;
        this.currentState = initialBoard(size);
        this.turnKeeper = new TurnKeeper(2);
        this.executor = new MoveExecutor(this);
    }

    /**
     * Construct a board with an existing state.
     */
    public Board(String boardState, TileColour currentColour) throws InvalidBoardException {
        int boardSize = (int) Math.sqrt(boardState.length());
        if (boardSize * boardSize != boardState.length()) {
            throw new InvalidBoardException("Board state of length unequal to n^2 provided");
        }

        this.currentState = boardState;
        this.size = boardSize;
        this.turnKeeper = new TurnKeeper(2, currentColour);
        this.executor = new MoveExecutor(this);
    }

    int size() {
        return this.size;
    }

    String currentState() {
        return this.currentState;
    }

    List<String> history() {
        return this.history;
    }

    public void update(int index, TileColour colour) throws InvalidMoveException {
        String nextState =  this.executor.apply(index, colour);
        this.history.add(this.currentState);
        this.currentState = nextState;
        turnKeeper.passTurn();
    }

    public TurnKeeper turnKeeper() {
        return this.turnKeeper;
    }

    private String initialBoard (int size) {
        StringBuilder board = new StringBuilder();

        for (int i = 0; i < size * size; i++) {
            board.append(TileColour.EMPTY);
        }
        return board.toString();
    }
}