package com.nedap.go.utilities;

public enum TileColour {
    EMPTY(0),
    BLACK(1),
    WHITE(2);

    private int number;

    TileColour(int number) {
        this.number = number;
    }

    public int asNumber() {
        return this.number;
    }
}
