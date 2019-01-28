package com.nedap.go.betago.sequences;

import com.nedap.go.betago.Sequence;
import com.nedap.go.utilities.Board;
import com.nedap.go.utilities.TileColour;
import com.nedap.go.utilities.exceptions.InvalidMoveException;
import com.nedap.go.utilities.exceptions.OutOfBoundsException;


public class CheckersSequence implements Sequence {
    private Integer currentCol = 0;
    private Integer currentRow = 1;
    private TileColour colour;
    private Board board;

    public CheckersSequence(Board board, TileColour colour) {
        this.board = board;
        this.colour = colour;
    }

    @Override
    public int next() {
        try {
            this.board.moveValidator().verify(indexFromCoordinates(currentCol, currentRow), this.colour);
            return indexFromCoordinates(currentCol, currentRow);
        } catch (OutOfBoundsException e) {
            return -1;
        }
        catch (InvalidMoveException e) {
            this.iterate();
            return this.next();
        }
    }

    private void iterate() {
        if (this.currentRow >  0) {
            this.currentCol++;
            this.currentRow--;
            return;
        }

        this.currentRow = this.currentCol + 2;
        this.currentCol = 0;

    }


    private Integer indexFromCoordinates (Integer x, Integer y) {
        return x + this.board.size() * y;
    }

    @Override
    public boolean remainsValid() {
        return true;
    }
}
