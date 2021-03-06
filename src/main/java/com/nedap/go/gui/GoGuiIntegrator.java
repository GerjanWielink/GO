package com.nedap.go.gui;

import com.nedap.go.utilities.TileColour;
import javafx.application.Platform;

public class GoGuiIntegrator implements GoGui {

	private GoGuiImpl wrappee;

	/**
	 * Creates a GoGUIIntegrator that is capable of configuring and controlling the
	 * GO GUI.
	 *
	 * @param boardSize            the desired initial board size.
	 */
	public GoGuiIntegrator(int boardSize, OnClickTileHandler moveHandler, OnClickPassHandler passHandler, TileColour colour, String opponentUsername) {
		createWrappedObject();
		wrappee.setInitialBoardSize(boardSize);
		wrappee.setOnClickTileHandler(moveHandler);
		wrappee.setOnClickPassHandler(passHandler);
		wrappee.setPlayerColour(colour);
		wrappee.setFrameTitle(opponentUsername);
	}


	public synchronized void updateTextMessage(String message) {
		Platform.runLater(() -> wrappee.updateTextMessage(message));
	}


	@Override
	public synchronized void setBoardSize(int size) {
		Platform.runLater(() -> wrappee.setBoardSize(size));
	}

	public synchronized int getBoardSize() {
		return wrappee.getBoardSize();
	}

	@Override
	public synchronized void addStone(int x, int y, boolean white) {
		Platform.runLater(() -> {
			try {
				wrappee.addStone(x, y, white);
			} catch (InvalidCoordinateException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public synchronized void removeStone(int x, int y) {
		Platform.runLater(() -> {
			try {
				wrappee.removeStone(x, y);
			} catch (InvalidCoordinateException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public synchronized void addAreaIndicator(int x, int y, boolean white) {
		Platform.runLater(() -> {
			try {
				wrappee.addAreaIndicator(x, y, white);
			} catch (InvalidCoordinateException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public synchronized void addHintIndicator(int x, int y) {
		Platform.runLater(() -> {
			try {
				wrappee.addHintIndicator(x, y);
			} catch (InvalidCoordinateException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public synchronized void removeHintIdicator() {
		Platform.runLater(() -> wrappee.removeHintIdicator());
	}

	@Override
	public synchronized void clearBoard() {
		Platform.runLater(() -> wrappee.clearBoard());
	}

	@Override
	public synchronized void startGUI() {
		startJavaFX();
		wrappee.waitForInitializationLatch();
	}

	@Override
	public synchronized void stopGUI() {
		// Not implemented yet
	}

	private void createWrappedObject() {
		if (wrappee == null) {
			GoGuiImpl.startGUI();

			while (!GoGuiImpl.isInstanceAvailable()) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			wrappee = GoGuiImpl.getInstance();
		}
	}

	private void startJavaFX() {
		createWrappedObject();
		wrappee.countDownConfigurationLatch();
	}
}
