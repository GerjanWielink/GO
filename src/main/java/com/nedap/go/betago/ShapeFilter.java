package com.nedap.go.betago;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShapeFilter {
    private int boardSize;
    private String regexp;
    private String replacementPattern;

    private static final String PADDING = "P";

    public ShapeFilter(int boardSize, String matchPattern, String replacementPattern) {
        this.boardSize = boardSize;
        this.regexp = matchPattern;
        this.replacementPattern = replacementPattern;
    }

    public boolean matches(String paddedBoard) {
        Matcher matcher = Pattern.compile(this.regexp).matcher(paddedBoard);
        return matcher.matches();
    }

    /**
     * When filtering out shapes we need to add a column of padding as we are considering the board in a
     * one-dimensional fashion which allows shapes to form over the edge of the board, which we want to avoid.
     * @param board
     * @return
     */
    public String filter(String board) {
        String updatedBoard = this.addPadding(board);

        while (this.matches(updatedBoard)) {
            updatedBoard = updatedBoard.replaceAll(this.regexp, this.replacementPattern);
        }

        return this.removePadding(updatedBoard);
    }

    /**
     * Adds an empty column to the right of the board to break edge cases in the pattern recognition
     * @param board
     * @return padded board
     */
    public String addPadding(String board) {
        StringBuilder paddedBoard = new StringBuilder();
        for (int i = 0; i < boardSize * boardSize; i += boardSize) {
            paddedBoard.append(board.substring(i, i + boardSize)).append(PADDING);
        }


        return paddedBoard.toString();
    }

    /**
     * Removes padding character from board
     * @param paddedBoard
     * @return sanitized board
     */
    private String removePadding(String paddedBoard) {
        return paddedBoard.replaceAll(PADDING, "");
    }
}
