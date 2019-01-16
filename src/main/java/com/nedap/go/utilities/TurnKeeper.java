package com.nedap.go.utilities;

import java.util.Arrays;
import java.util.List;

public class TurnKeeper {
    private int current = 0;
    private List<TileColour> players;
    private static final List<TileColour> availableColours = Arrays.asList(TileColour.BLACK, TileColour.WHITE);

    TurnKeeper(int playerCount) {
        if(playerCount != 2) {
            // TODO: EXCEPTION
            return;
        }

        this.players = availableColours.subList(0, 2);
    }

    TurnKeeper(int playerCount, TileColour alternativeFirstColour) {
        if(playerCount != 2) {
            // TODO: EXCEPTION
            return;
        }

        this.players = availableColours.subList(0, 2);
        this.current = this.players.indexOf(alternativeFirstColour);
    }

    public void passTurn() {
        current = (current + 1) % players.size();
    }

    public boolean hasTurn(TileColour colour) {
        return this.players.get(current).equals(colour);
    }

    public TileColour current () {
        return this.players.get(this.current);
    }

    public TileColour next () {
        return this.players.get((this.current + 1) % this.players.size());
    }

    public List<TileColour> players() {
        return this.players;
    }
}
