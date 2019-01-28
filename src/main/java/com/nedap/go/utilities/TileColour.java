package com.nedap.go.utilities;

public enum TileColour {
    EMPTY(0),
    BLACK(1),
    WHITE(2);

    private int number;

    TileColour(int number) {
        this.number = number;
    }

    public char asChar() {
        return Character.forDigit(this.number, 10);
    }

    public int asInt() {
        return this.number;
    }

    public TileColour other() {
        return this == BLACK ? WHITE : BLACK;
    }
}
