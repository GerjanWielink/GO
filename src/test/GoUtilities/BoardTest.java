package GoUtilities;

import GoUtilities.Exceptions.OutOfBoundsException;
import org.junit.Before;
import org.junit.Test;
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
}
