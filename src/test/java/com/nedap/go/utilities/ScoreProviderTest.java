package com.nedap.go.utilities;

import com.nedap.go.utilities.exceptions.InvalidBoardException;
import com.nedap.go.utilities.exceptions.InvalidMoveException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ScoreProviderTest {
    @Test
    public void shouldCalculateScoreOnSingleStone(){
        try {
            String boardState = "00000" +
                                "01000" +
                                "00000" +
                                "00000" +
                                "00000";
            Board board = new Board(boardState, TileColour.WHITE);
            ScoreProvider scoreProvider = board.scoreProvider();

            assertEquals(expectedScore(25.0, 0.5), scoreProvider.getScore());
        } catch (InvalidBoardException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldCalculateScoreOnTwoStones(){
        try {
            String boardState = "00000" +
                                "01000" +
                                "00000" +
                                "00200" +
                                "00000";
            Board board = new Board(boardState, TileColour.WHITE);
            ScoreProvider scoreProvider = board.scoreProvider();

            assertEquals(expectedScore(1.0, 1.5), scoreProvider.getScore());
        } catch (InvalidBoardException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldCalculateScoreOnSingleGroup(){
        try {
            String boardState = "00000" +
                    "01000" +
                    "01110" +
                    "00222" +
                    "00200";
            Board board = new Board(boardState, TileColour.WHITE);
            ScoreProvider scoreProvider = board.scoreProvider();

            assertEquals(expectedScore(4.0, 6.5), scoreProvider.getScore());
        } catch (InvalidBoardException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldCalculateScoreOnMultipleGroups(){
        try {
            String boardState = "01000" +
                                "01000" +
                                "01111" +
                                "02222" +
                                "00200";
            Board board = new Board(boardState, TileColour.WHITE);
            ScoreProvider scoreProvider = board.scoreProvider();

            assertEquals(expectedScore(12, 7.5), scoreProvider.getScore());
        } catch (InvalidBoardException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldCalculateScoreOnLargeBoard(){
        try {
            Board board = new Board(19);
            board.tryMove(0, TileColour.BLACK);
            ScoreProvider scoreProvider = board.scoreProvider();

            assertEquals(expectedScore(19 * 19, 0.5), scoreProvider.getScore());
        } catch (InvalidMoveException e) {
            fail(e.getMessage());
        }
    }


    private Map<TileColour, Double> expectedScore(double black, double white) {
        Map<TileColour, Double> expected = new HashMap<>();

        expected.put(TileColour.BLACK, black);
        expected.put(TileColour.WHITE, white);

        return expected;
    }
}
