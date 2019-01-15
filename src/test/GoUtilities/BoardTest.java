package GoUtilities;

import GoUtilities.Exceptions.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BoardTest {
    private static final int SIZE = 9;
    private Board board;

    @Before
    public void setup () {
        this.board = new Board(SIZE);
    }

    @Test
    public void shouldThrowOutOfBounds() {
        assertThrows(OutOfBoundsException.class, () -> {
            this.board.update(SIZE * SIZE + 1, TileColour.BLACK);
        });
    }

    @Test
    public void shouldAcceptValidMove() {
        try {
            this.board.update(SIZE, TileColour.BLACK);
            this.board.update(SIZE + 1, TileColour.WHITE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail("Unexpected exception thrown");
        }
    }

    @Test
    public void shouldThrowBeforeTurnException() {
        assertThrows(BeforeTurnException.class, () -> this.board.update(SIZE * SIZE + 1, TileColour.WHITE));
    }

    @Test
    public void shouldThrowKoException() {
        String almostKoBoardState = "0120" + // 01:02:03:04    | B W |
                                    "1012" + // 05:06:07:08    B | B W
                                    "0120" + // 09:10:11:12    | B W |
                                    "0000";  // 13:14:15:16    | | | |
        try {
            Board almostKoBoard = new Board(almostKoBoardState, TileColour.WHITE);
            almostKoBoard.update(6, TileColour.WHITE);
            assertThrows(KoException.class, () -> almostKoBoard.update(7, TileColour.BLACK));
        } catch (InvalidBoardException e) {
            fail(e.getMessage());
        } catch (InvalidMoveException e) {
            fail(e.getMessage());
        }


    }
}
