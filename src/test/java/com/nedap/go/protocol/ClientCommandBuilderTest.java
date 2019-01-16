package com.nedap.go.protocol;

import com.nedap.go.utilities.TileColour;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClientCommandBuilderTest {

    @Test
    public void shouldFormatHandshakeCommand() {
        String actual = ClientCommandBuilder.handshake("Thiery Baudet");
        String expected = "HANDSHAKE+Thiery Baudet";

        assertEquals(expected, actual);
    }

    @Test
    public void shouldFormatSetConfigCommand () {
        String actual = ClientCommandBuilder.setConfig(0, TileColour.BLACK, 19);
        String expected = "SET_CONFIG+0+1+19";

        assertEquals(expected, actual);
    }

    @Test
    public void shouldFormatSetConfigWithDefaultColour () {
        String actual = ClientCommandBuilder.setConfig(1, 20);
        String expected = "SET_CONFIG+1+0+20";

        assertEquals(expected, actual);
    }

    @Test
    public void shouldFormatSetConfigWithDefaults () {
        String actual = ClientCommandBuilder.setConfig(2);
        String expected = "SET_CONFIG+2+0+19";

        assertEquals(expected, actual);
    }

    @Test
    public void shouldFormatMove () {
        String actual = ClientCommandBuilder.move(3, "Thiery Baudet", 29);
        String expected = "MOVE+3+Thiery Baudet+29";

        assertEquals(expected, actual);
    }

    @Test
    public void shouldFormatPass () {
        String actual = ClientCommandBuilder.pass(4, "Baas B");
        String expected = "PASS+4+Baas B";
    }
}
