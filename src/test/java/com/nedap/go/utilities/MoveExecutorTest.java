package com.nedap.go.utilities;

import com.nedap.go.utilities.exceptions.NotEmptyException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoveExecutorTest {
    private MoveExecutor executor;
    private Board board;

    @Before
    public void setup() {
        this.board = new Board(5);
        this.executor = new MoveExecutor(this.board);
    }

    @Test
    public void shouldThrowNotEmptyException () {
        assertThrows(NotEmptyException.class, () -> {
            this.board.tryMove(0, TileColour.BLACK);
            this.board.tryMove(0, TileColour.WHITE);
        });
    }


    // TODO: Move to BoardTest
//    /**
//     * Board (5 * 5) index cheatsheet
//     *
//     * 00 01 02 03 04
//     * 05 06 07 08 09
//     * 10 11 12 13 14
//     * 15 16 17 18 19
//     * 20 21 22 23 24
//     */
//    @Test
//    public void shouldProperlyProvideNeighbours () {
//        Set<Integer> expected = new HashSet<>(Arrays.asList(0, 2, 6));
//        assertEquals(expected, this.executor.getNeighbourIndices(1));
//
//        expected = new HashSet<>(Arrays.asList(19, 23));
//        assertEquals(expected, this.executor.getNeighbourIndices(24));
//
//        expected = new HashSet<>(Arrays.asList(7, 11, 13, 17));
//        assertEquals(expected, this.executor.getNeighbourIndices(12));
//
//        expected = new HashSet<>(Arrays.asList(10, 16, 20));
//        assertEquals(expected, this.executor.getNeighbourIndices(15));
//    }

}
