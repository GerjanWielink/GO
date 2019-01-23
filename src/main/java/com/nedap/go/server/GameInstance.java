package com.nedap.go.server;

import com.nedap.go.protocol.ServerMessageBuilder;
import com.nedap.go.server.exceptions.GameFullException;
import com.nedap.go.utilities.Board;
import com.nedap.go.utilities.TileColour;
import com.nedap.go.utilities.exceptions.InvalidMoveException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameInstance {
    private static final int NUM_PLAYERS = 2;

    private int id;
    private GameState gameState;
    private GameManager gameManager;
    private ClientHandler playerOne;
    private ClientHandler playerTwo;
    private Map<ClientHandler, TileColour> playerRoles;
    private boolean hasConfig;
    private Board board;

    public GameInstance(int id, GameManager gameManager) {
        this.id = id;
        this.gameManager = gameManager;
        this.gameState = GameState.WAITING;
        this.hasConfig = false;

        Logger.log("New empty game instance created");
    }

    public GameInstance(int id, GameManager gameManager,  ClientHandler client) {
        this.id = id;
        this.gameState = GameState.WAITING;
        this.gameManager = gameManager;
        this.hasConfig = false;
        try {
            this.addPlayer(client);
        } catch (GameFullException e) {
            Logger.error(e.getMessage());
        }

        Logger.log("New game instance with id (" + id + ") created for client " + client.username());
    }


    public synchronized void provideConfig(TileColour colour, int boardSize) {
        this.board = new Board(boardSize);

        this.playerRoles = new HashMap<>();
        playerRoles.put(playerOne, colour);

        this.hasConfig = true;

        if (playerTwo != null) {
            playerRoles.put(playerTwo, colour.other());
            this.startGame();
        }
    }

    public synchronized void addPlayer(ClientHandler player) throws GameFullException {
        if(this.playerOne == null) {
            this.playerOne = player;
            player.addToGame(this, true);
            Logger.log("Player " + player.username() + " connected to game " + this.id + " as game leader");
            return;
        }

        if(this.playerTwo == null) {
            this.playerTwo = player;
            player.addToGame(this, false);
            Logger.log("Player " + player.username() + " connected to game " + this.id);

            if (this.hasConfig) {
                playerRoles.put(this.playerTwo, playerRoles.get(this.playerOne).other());
                this.startGame();
            }

            return;
        }

        throw new GameFullException();
    }

    // TODO: implement boolean similar to tryPass indicating if there are any valid moves left
    public void tryMove(int index, TileColour colour) throws InvalidMoveException {
        this.board.tryMove(index, colour);

        playerOne.sendOutbound(ServerMessageBuilder.acknowledgeMove(this.id, index, colour, this));
        playerTwo.sendOutbound(ServerMessageBuilder.acknowledgeMove(this.id, index, colour, this));
    }

    public void tryPass(TileColour colour) throws InvalidMoveException {
       if(!this.board.tryPass(colour)) {
           this.endGame();
       } else {
           this.playerOne.sendOutbound(ServerMessageBuilder.acknowledgeMove(this.id, -1, colour, this));
           this.playerTwo.sendOutbound(ServerMessageBuilder.acknowledgeMove(this.id, -1, colour, this));
       }
    }

    public void startGame() {
        this.gameState = GameState.PLAYING;
        this.playerOne.notifyGameStart(this.playerRoles.get(this.playerOne));
        this.playerTwo.notifyGameStart(this.playerRoles.get(this.playerTwo));
    }

    public void endGame() {
        this.gameState = GameState.FINISHED;

        Map<TileColour, Double> score = this.board.scoreProvider().getScore();
        TileColour winningColour = score.get(TileColour.BLACK) > score.get(TileColour.WHITE) ? TileColour.BLACK : TileColour.WHITE;

        String winnerName = this.playerRoles.get(this.playerOne).equals(winningColour) ? playerOne.username() : playerTwo.username();

        this.playerOne.sendOutbound(ServerMessageBuilder.gameFinished(this.id, winnerName, score, ""));
        this.playerTwo.sendOutbound(ServerMessageBuilder.gameFinished(this.id, winnerName, score, ""));

        this.gameManager.endGame(this);
    }

    public boolean isFull() {
        return this.playerOne != null && this.playerTwo != null;
    }

    public List<ClientHandler> getPlayers() {
        return Arrays.asList(playerOne, playerTwo);
    }

    public int id() {
        return this.id;
    }

    public Board board() {
        return this.board;
    }

    public GameState gameState() {
        return this.gameState;
    }
}
