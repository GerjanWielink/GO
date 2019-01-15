package GoUtilities;

import GoUtilities.Exceptions.*;

import java.util.List;
import java.util.stream.Stream;

public class MoveVerifyer {
    private Board board;

    public MoveVerifyer (Board board) {
        this.board = board;
    }

    public String verifyAndApply (int index, TileColour colour) throws InvalidMoveException {
        enforceColour(colour);
        enforceBounds(index);
        enforceEmpty(index);
        enforceKo(index, colour);

        return nextState(index, colour);
    }

    private void enforceBounds (int index) throws OutOfBoundsException {
        if (index < 0 || index > board.size()) {
            throw new OutOfBoundsException();
        }
    }

    private void enforceColour (TileColour colour) throws BeforeTurnException {
        if (colour != this.board.currentPlayer() ) {
            throw new BeforeTurnException();
        }
    }


    private void enforceEmpty (int index) throws NotEmptyException {

    }

    private void enforceKo (int index, TileColour colour) throws KoException {
        String optimisticNextState = this.nextState(index, colour);
        List<String> history = this.board.history();

        for (String state : history) {
            if (state.equals(optimisticNextState)) {
                throw new KoException();
            }
        }
    }

    private String nextState(int index, TileColour colour) {
        StringBuilder nextBoardState = new StringBuilder(this.board.currentState());

        nextBoardState.setCharAt(index, (char)(colour.asNumber() + '0'));

        return nextBoardState.toString();
    }
}
