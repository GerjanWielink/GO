package GoUtilities;

import GoUtilities.Exceptions.InvalidMoveException;
import javafx.util.Pair;

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
     * an exception is thrown.
     */
    public String apply(int index, TileColour colour) throws InvalidMoveException {
        this.validator.verify(index, colour);
        return applyOptimistic(index, colour);
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

    /**
     * @param boardState previous boardState
     * @param colour colour for which captures should be removed
     * @return string containing previous board with the respective captures removed
     */
    public String removeCaptures(String boardState, TileColour colour) {
        // TODO: take a long hard look at this
        Set<Integer> tilesOfColourUnchecked = extractTilesOfColour(boardState, colour);
        Set<Integer> tilesToBeRemoved = new HashSet<>();

        while(tilesOfColourUnchecked.size() > 0) {
            Pair<Boolean, Set<Integer> > consideredTiles = checkGroupFromTile(boardState, colour,(int) tilesOfColourUnchecked.toArray()[0], null);

            if(!consideredTiles.getKey()) {
                tilesToBeRemoved.addAll(consideredTiles.getValue());
            }

            tilesOfColourUnchecked.removeAll(consideredTiles.getValue());
        }

        StringBuilder nextBoardStateBuilder = new StringBuilder(boardState);
        tilesToBeRemoved.forEach(tileIndex -> {
            nextBoardStateBuilder.setCharAt(tileIndex, Character.forDigit(TileColour.EMPTY.asNumber(), 10));
        });

        return nextBoardStateBuilder.toString();
    }

    public Pair<Boolean, Set<Integer>> checkGroupFromTile(String boardState, TileColour colour, int index, Set<Integer> group) {
        Set<Integer> captureGroup = group != null ? group : new HashSet<>();
        Set<Integer> neighbours = getNeighbourIndices(index);
        boolean groupFree = false;


        for (int neighbourIndex: neighbours){
            // already considered
            if(captureGroup.contains(neighbourIndex)) {
                continue;
            }
            // neighbour is empty
            if(boardState.charAt(neighbourIndex) + '0' == TileColour.EMPTY.asNumber()) {
                groupFree = true;
                continue;
            }
            // neighbour is of same colour
            if(boardState.charAt(neighbourIndex) + '0' == colour.asNumber()) {
                captureGroup.add(neighbourIndex);
                Pair<Boolean, Set<Integer>> neighbourResult = checkGroupFromTile(boardState, colour, neighbourIndex, captureGroup);

                captureGroup.addAll(neighbourResult.getValue());
                groupFree = groupFree || neighbourResult.getKey();
            }
            // neighbour is of other colour, do nothing
        }

        return new Pair<>(groupFree, captureGroup);
    }

    public Set<Integer> extractTilesOfColour(String boardState, TileColour colour) {
        Set<Integer> tilesOfColour = new HashSet<>();

        for (int i = 0; i < boardState.length(); i++) {
            if(boardState.charAt(i) + '0' == colour.asNumber()) {
                tilesOfColour.add(i);
            }
        }

        return tilesOfColour;
    }

    public Set<Integer> getNeighbourIndices(int index) {
        Set<Integer> neighbours = new HashSet<>();

        // not on top row
        if (index > this.board.size()) {
            neighbours.add(index - this.board.size());
        }
        // not on bottom row
        if (index < (this.board.size() - 1) * this.board.size()) {
            neighbours.add(index + this.board.size());
        }
        // not on left edge
        if (index % this.board.size() != 0) {
            neighbours.add(index - 1);
        }
        // not on right edge
        if ((index + 1) % this.board.size() != 0) {
            neighbours.add(index + 1);
        }

        return neighbours;
    }
}
