package com.nedap.go.gui;

import com.nedap.go.utilities.TileColour;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GoGuiImpl extends Application {

	private final static int INITIAL_BOARD_SIZE = 19;
	private final static int INITIAL_SQUARE_SIZE = 50;

	private int currentBoardSize = INITIAL_BOARD_SIZE;
	private int currentSquareSize = INITIAL_SQUARE_SIZE;

	private Node[][] board = null;
	private List<Line> boardLines = new ArrayList<>();
	private Group root = null;
	private Stage primaryStage = null;
	private Node hint = null;
	private TileColour playerColour;
	private Text text;
	private String frameTitle;

	private static final CountDownLatch waitForConfigurationLatch = new CountDownLatch(1);
	private static final CountDownLatch initializationLatch = new CountDownLatch(1);

	private OnClickTileHandler onClickTileHandler;
	private OnClickPassHandler onClickPassHandler;

	private static GoGuiImpl instance;

	protected static boolean isInstanceAvailable() {
		return instance != null;
	}

	public static GoGuiImpl getInstance() {
		return instance;
	}

	protected void countDownConfigurationLatch() {
		waitForConfigurationLatch.countDown();
	}

	@Override
	public void start(Stage primaryStage) {
		instance = this;

		try {
			waitForConfigurationLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.primaryStage = primaryStage;

		String title = this.frameTitle != null ? this.frameTitle : "GO!";

		primaryStage.setTitle(title);

		initNewBoard();

		initializationLatch.countDown();

	}

	public void setFrameTitle(String title) {
		this.frameTitle = title;
	}


	private void initNewBoard() {
		root = new Group();
		board = new Node[currentBoardSize][currentBoardSize];

		Scene scene = new Scene(root,
				(currentBoardSize + 1) * currentSquareSize,
				(currentBoardSize + 2) * currentSquareSize);
		primaryStage.setScene(scene);
		primaryStage.show();

		scene.setFill(Color.SALMON);

		initBoardLines();
		addPassButton();
		addMessage();
		addLocationIndicators();
	}

	private void initBoardLines() {
		root.getChildren().removeAll(boardLines);
		boardLines.clear();

		int squareSize = currentSquareSize;

		// Draw horizontal lines
		for (int i = 1; i <= currentBoardSize; i++) {
			boardLines.add(new Line(squareSize, i * squareSize, currentBoardSize * squareSize, i * squareSize));
		}

		// Draw vertical lines
		for (int i = 1; i <= currentBoardSize; i++) {
			boardLines.add(new Line(i * squareSize, squareSize, i * squareSize, currentBoardSize * squareSize));
		}

		root.getChildren().addAll(boardLines);


		hint = new Circle(currentSquareSize / 2);
		((Circle) hint).setFill(Color.YELLOW);

		hint.setVisible(false);
		root.getChildren().add(hint);
	}

	protected void addStone(int x, int y, boolean white) throws InvalidCoordinateException {
		checkCoordinates(x, y);
		removeStone(x, y);

		Circle newStone = new Circle(((x + 1) * currentSquareSize), ((y + 1) * currentSquareSize),
					currentSquareSize / 2);

		if (white) {
			newStone.setFill(Color.WHITE);
		} else {
			newStone.setFill(Color.BLACK);
		}

		board[x][y] = newStone;
		root.getChildren().add(newStone);
	}

	protected void removeStone(int x, int y) throws InvalidCoordinateException {
		checkCoordinates(x, y);

		if (board[x][y] != null) {
			root.getChildren().remove(board[x][y]);
			addOnHoverTile(x, y, true);
		}
		addOnHoverTile(x, y, this.playerColour == TileColour.WHITE);
	}

	protected void addAreaIndicator(int x, int y, boolean white) throws InvalidCoordinateException {
		checkCoordinates(x, y);
		removeStone(x, y);

		Rectangle areaStone = new Rectangle(((x + 1) * currentSquareSize) - currentSquareSize / 6,
				((y + 1) * currentSquareSize) - currentSquareSize / 6, currentSquareSize / 3,
				currentSquareSize / 3);
		areaStone.setFill(white ? Color.WHITE : Color.BLACK);
		board[x][y] = areaStone;
		root.getChildren().add(areaStone);
	}

	protected void addHintIndicator(int x, int y) throws InvalidCoordinateException {
		hint.setTranslateX(((x + 1) * currentSquareSize));
		hint.setTranslateY(((y + 1) * currentSquareSize));
		hint.setVisible(true);
	}

	protected void removeHintIdicator() {
		hint.setVisible(false);
	}

	private void checkCoordinates(int x, int y) throws InvalidCoordinateException {
		if (x < 0 || x >= currentBoardSize) {
			throw new InvalidCoordinateException("x coordinate is outside of board range. x coordinate: " + x
					+ " board range: 0-" + (currentBoardSize - 1));
		}

		if (y < 0 || y >= currentBoardSize) {
			throw new InvalidCoordinateException("y coordinate is outside of board range. y coordinate: " + y
					+ " board range: 0-" + (currentBoardSize - 1));
		}
	}

	protected void clearBoard() {
		try {
			for (int x = 0; x < currentBoardSize; x++) {
				for (int y = 0; y < currentBoardSize; y++) {
					removeStone(x, y);
				}
			}
		} catch (InvalidCoordinateException e) {
			throw new IllegalStateException(e);
		}
	}

	protected void setBoardSize(int size) {
		currentBoardSize = size;
		initNewBoard();
	}

	protected int getBoardSize() {
		return currentBoardSize;
	}

	protected void setInitialBoardSize(int size) {
		currentBoardSize = size;
	}

	protected static void startGUI() {
		new Thread(() -> Application.launch(GoGuiImpl.class)).start();
	}

	protected void waitForInitializationLatch() {
		try {
			if (!initializationLatch.await(30, TimeUnit.SECONDS)) {
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	////////////////////////////////////
	////////////////////////////////////
	//////////// CUSTOM CODE ///////////
	////////////////////////////////////
	////////////////////////////////////
	/**
	 * Hoverable tiles
	 * @throws InvalidCoordinateException
	 */
	protected void addOnHoverTile(int x, int y, boolean white) throws InvalidCoordinateException {
		checkCoordinates(x, y);
		Circle newStone = new Circle(((x + 1) * currentSquareSize), ((y + 1) * currentSquareSize),
				currentSquareSize / 2);

		newStone.setFill(Color.TRANSPARENT);
		OnClickTileHandler handler = this.onClickTileHandler;

		newStone.setOnMouseClicked(event -> handler.handle(x, y));

		newStone.setOnMouseEntered(event -> {
			newStone.setFill(white ? Color.WHITE : Color.BLACK);
		});

		newStone.setOnMouseExited(event -> {
			newStone.setFill(Color.TRANSPARENT);
		});

		board[x][y] = newStone;
		root.getChildren().add(newStone);
	}


	private void addPassButton () {
		Button passButton = new Button("PASS");
		passButton.setLayoutX(currentSquareSize);
		passButton.setLayoutY((currentBoardSize + 1 ) * currentSquareSize);
		passButton.setOnMouseClicked(event -> onClickPassHandler.handle());

		root.getChildren().add(passButton);
	}
	private Circle locationIndicator (int offCenterX, int offCenterY){
		return new Circle(
				currentSquareSize * (1 + (currentBoardSize / 2) + offCenterX),
				currentSquareSize * (1 + (currentBoardSize / 2) + offCenterY),
				currentSquareSize / 12
		);
	}


	private void addLocationIndicators() {
		root.getChildren().add(locationIndicator(0, 0));

		if (currentBoardSize == 19) {
			root.getChildren().add(locationIndicator(6, 0));
			root.getChildren().add(locationIndicator(-6, 0));
			root.getChildren().add(locationIndicator(0, 6));
			root.getChildren().add(locationIndicator(0, -6));
			root.getChildren().add(locationIndicator(6, 6));
			root.getChildren().add(locationIndicator(-6, -6));
			root.getChildren().add(locationIndicator(-6, 6));
			root.getChildren().add(locationIndicator(6, -6));
		}
	}

	private void addMessage () {
		this.text = new Text("");

		this.text.setFont(Font.font ("Verdana", 12));

		this.text.setLayoutY(currentSquareSize / 2);
		this.text.setLayoutX(currentSquareSize);

		root.getChildren().add(this.text);
	}

	public void updateTextMessage(String message) {
		root.getChildren().remove(this.text);
		this.text.setText(message);
		root.getChildren().add(this.text);
	}

	protected void setOnClickTileHandler(OnClickTileHandler onClickTileHandler) {
		this.onClickTileHandler = onClickTileHandler;
	}

	protected void setOnClickPassHandler(OnClickPassHandler onClickPassHandler) {
		this.onClickPassHandler = onClickPassHandler;
	}

	protected void setPlayerColour (TileColour colour) {
		this.playerColour = colour;
	}
}
