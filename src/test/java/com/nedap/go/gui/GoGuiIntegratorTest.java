package com.nedap.go.gui;

import static org.junit.Assert.assertEquals;

import com.nedap.go.utilities.TileColour;
import org.junit.Test;

/**
 * Example test class
 */
public class GoGuiIntegratorTest {

	@Test
	public void boardSizeTest() {
		GoGuiIntegrator goGuiIntegrator = new GoGuiIntegrator(true, true, 10, (x, y) -> {System.out.println(x + "," + y);}, TileColour.BLACK);
		assertEquals(10, goGuiIntegrator.getBoardSize());
	}

}
