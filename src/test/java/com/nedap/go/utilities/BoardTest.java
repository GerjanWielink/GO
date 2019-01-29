package com.nedap.go.utilities;

import com.nedap.go.utilities.exceptions.*;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
            this.board.tryMove(SIZE * SIZE + 1, TileColour.BLACK);
        });
    }

    @Test
    public void shouldAcceptValidMove() {
        try {
            this.board.tryMove(SIZE, TileColour.BLACK);
            this.board.tryMove(SIZE + 1, TileColour.WHITE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail("Unexpected exceptions thrown");
        }
    }

    @Test
    public void shouldThrowBeforeTurnException() {
        assertThrows(BeforeTurnException.class, () -> this.board.tryMove(0, TileColour.WHITE));
    }

    @Test
    public void shouldThrowKoException() {
        String almostKoBoardState = "0120" + // 00:01:02:03    | B W |
                                    "1012" + // 04:05:06:07    B | B W
                                    "0120" + // 08:09:10:11    | B W |
                                    "0000";  // 12:13:14:15    | | | |
        try {
            Board almostKoBoard = new Board(almostKoBoardState, TileColour.WHITE);
            try {
                almostKoBoard.tryMove(5, TileColour.WHITE);
            } catch (InvalidMoveException e) {
                fail(e.getMessage());
            }
            assertThrows(KoException.class, () -> almostKoBoard.tryMove(6, TileColour.BLACK));
        } catch (InvalidBoardException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldNotThrowKoException() {
        String almostKoBoardState = "0120" + // 00:01:02:03    | B W |
                                    "1012" + // 04:05:06:07    B | B W
                                    "0120" + // 08:09:10:11    | B W |
                                    "0000";  // 12:13:14:15    | | | |
        try {
            Board almostKoBoard = new Board(almostKoBoardState, TileColour.WHITE);
            almostKoBoard.tryMove(5, TileColour.WHITE);
            almostKoBoard.tryMove(11, TileColour.BLACK);
            almostKoBoard.tryMove(12, TileColour.WHITE);
            almostKoBoard.tryMove(6, TileColour.BLACK);
        } catch (InvalidBoardException e) {
            fail(e.getMessage());
        } catch (InvalidMoveException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldRemoveLargeCaptureGroup() {
        String initialBoard = "0020000" + // 00:01:02:03:04:05:06
                              "0212000" + // 07:08:09:10:11:12:13
                              "0212000" + // 14:15:16:17:18:19:20
                              "0212000" + // 21:22:23:24:25:26:27
                              "0212000" + // 28:29:30:31:32:33:34
                              "0000000" + // 35:36:37:38:39:40:41
                              "0000000";  // 42:43:44:45:46:47:48

        String expectedBoard = "0020000" + // 00:01:02:03:04:05:06
                               "0202000" + // 07:08:09:10:11:12:13
                               "0202000" + // 14:15:16:17:18:19:20
                               "0202000" + // 21:22:23:24:25:26:27
                               "0202000" + // 28:29:30:31:32:33:34
                               "0020000" + // 35:36:37:38:39:40:41
                               "0000000";  // 42:43:44:45:46:47:48

        try {
            Board largeCaptureBoard = new Board(initialBoard, TileColour.WHITE);
            largeCaptureBoard.printCurrentState();
            largeCaptureBoard.tryMove(37, TileColour.WHITE);
            largeCaptureBoard.printCurrentState();

            assertEquals(expectedBoard, largeCaptureBoard.currentState());

        } catch (InvalidBoardException e) {
            fail(e.getMessage());
        } catch (InvalidMoveException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldThrowInvalidBoardException() {
        assertThrows(InvalidBoardException.class, () -> new Board("000", TileColour.BLACK));
    }

    @Test
    public void shouldPrintCurrentState() {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outStream));

        this.board.printCurrentState();

        String expected = "000000000\n" +
                        "000000000\n" +
                        "000000000\n" +
                        "000000000\n" +
                        "000000000\n" +
                        "000000000\n" +
                        "000000000\n" +
                        "000000000\n" +
                        "000000000\n";

        assertEquals(expected, outStream.toString());
    }
}
