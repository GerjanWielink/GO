package com.nedap.go.utilities;

import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TurnKeeperTest {
    private TurnKeeper turnKeeper;

    @Before
    public void setup() {
        this.turnKeeper = new TurnKeeper(2);
    }

    @Test
    public void blackHasFirstTurn() {
        assertTrue(this.turnKeeper.hasTurn(TileColour.BLACK));
        assertFalse(this.turnKeeper.hasTurn(TileColour.WHITE));
    }

    @Test
    public void shouldPassTheTurn() {
        this.turnKeeper.passTurn();
        assertTrue(this.turnKeeper.hasTurn(TileColour.WHITE));
    }

    @Test
    public void shouldPassTheTurnMultipleTimes() {
        // BLACK
        this.turnKeeper.passTurn(); // WHITE
        this.turnKeeper.passTurn(); // BLACK

        this.turnKeeper.passTurn(); // WHITE
        assertTrue(this.turnKeeper.hasTurn(TileColour.WHITE));
        assertFalse(this.turnKeeper.hasTurn(TileColour.BLACK));

        this.turnKeeper.passTurn(); // BLACK
        assertTrue(this.turnKeeper.hasTurn(TileColour.BLACK));
        assertFalse(this.turnKeeper.hasTurn(TileColour.WHITE));
    }
}
