package GoUtilities;

import GoUtilities.Exceptions.InvalidMoveException;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private int size;
    private TileColour currentPlayer;
    private String currentState;
    private List<String> history = new ArrayList<>();
    private MoveVerifyer verifyer;

    /**
     * Construct an empty board of a given size.
     * @param size size of the board
     */
    public Board(int size) {
        this.size = size;
        this.currentState = initialBoard(size);
        this.currentPlayer = TileColour.BLACK;
        this.verifyer = new MoveVerifyer(this);
    }

    int size() {
        return this.size;
    }

    TileColour currentPlayer () {
        return currentPlayer;
    }

    String currentState() {
        return this.currentState;
    }

    List<String> history() {
        return this.history;
    }

    public void update(int index, TileColour colour) throws InvalidMoveException {
        String nextState =  this.verifyer.verifyAndApply(index, colour);
        this.history.add(this.currentState);
        this.currentState = nextState;
    }
    public boolean equals(Board board) {
        return this.currentState.equals(board.currentState()) && this.history.equals(board.history());
    }

    private String initialBoard (int size) {
        StringBuilder board = new StringBuilder();

        for (int i = 0; i < size * size; i++) {
            board.append(TileColour.EMPTY);
        }
        return board.toString();
    }
}