package com.nedap.go.server;

import com.nedap.go.protocol.ServerMessageBuilder;
import com.nedap.go.server.exceptions.GameFullException;
import com.nedap.go.utilities.Board;
import com.nedap.go.utilities.Logger;
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

    private Boolean playerOneWantsRematch;
    private Boolean playerTwoWantsRematch;

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


    /**
     * Receives config from the client handler and starts the game accordingly
     * @param colour Preferred tile colour
     * @param boardSize Preferred board size
     */
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

    /**
     *
     * @param player client handler
     * @param rematch indicates whether a rematch is requested.
     */
    public void requestRematch(ClientHandler player, boolean rematch) {
        // TODO: Convoluted..
        if (player == this.playerOne) {
            this.playerOneWantsRematch = rematch;
        } else {
            this.playerTwoWantsRematch = rematch;
        }

        if (
                (playerOneWantsRematch != null && !playerOneWantsRematch) ||
                (playerTwoWantsRematch != null && !playerTwoWantsRematch)
        ) {
            this.playerOne.sendOutbound(ServerMessageBuilder.acknowledgeRematch(false));
            this.playerTwo.sendOutbound(ServerMessageBuilder.acknowledgeRematch(false));
        }

        if (
                (playerOneWantsRematch != null && playerOneWantsRematch) &&
                (playerTwoWantsRematch != null && playerTwoWantsRematch)
        ) {
            this.playerOne.sendOutbound(ServerMessageBuilder.acknowledgeRematch(true));
            this.playerTwo.sendOutbound(ServerMessageBuilder.acknowledgeRematch(true));

            this.board = new Board(this.board.size());
            this.startGame();
        }
    }

    /**
     * Add a player into this instance and assign the proper role.
     * @param player client handler
     * @throws GameFullException
     */
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

    /**
     * Apply a move to the game and notify the players
     * @param index one-dimensional tile index
     * @param colour tile colour
     * @throws InvalidMoveException
     */
    public void tryMove(int index, TileColour colour) throws InvalidMoveException {
        // TODO: implement boolean similar to tryPass indicating if there are any valid moves left
        this.board.tryMove(index, colour);

        playerOne.sendOutbound(ServerMessageBuilder.acknowledgeMove(this.id, index, colour, this));
        playerTwo.sendOutbound(ServerMessageBuilder.acknowledgeMove(this.id, index, colour, this));
    }

    /**
     * apply a pass to the board. End the game if the game notifies that this was the second pass.
     * @param colour tile colour
     * @throws InvalidMoveException
     */
    public void tryPass(TileColour colour) throws InvalidMoveException {
       if(!this.board.tryPass(colour)) {
           this.endGame();
       } else {
           this.playerOne.sendOutbound(ServerMessageBuilder.acknowledgeMove(this.id, -1, colour, this));
           this.playerTwo.sendOutbound(ServerMessageBuilder.acknowledgeMove(this.id, -1, colour, this));
       }
    }

    /**
     * Called when both players have connected and a config has been set.
     * Notifies the players that the game is ready to be played
     */
    private void startGame() {
        this.gameState = GameState.PLAYING;
        this.playerOne.notifyGameStart(this.playerRoles.get(this.playerOne), this.playerTwo.username());
        this.playerTwo.notifyGameStart(this.playerRoles.get(this.playerTwo), this.playerOne.username());
    }

    /**
     * Called when the game stops. Sends out the scores and a rematch request
     */
    private void endGame() {
        this.gameState = GameState.FINISHED;

        Map<TileColour, Double> score = this.board.scoreProvider().getScore();
        TileColour winningColour = score.get(TileColour.BLACK) > score.get(TileColour.WHITE) ? TileColour.BLACK : TileColour.WHITE;

        String winnerName = this.playerRoles.get(this.playerOne).equals(winningColour) ? playerOne.username() : playerTwo.username();

        this.playerOne.sendOutbound(ServerMessageBuilder.gameFinished(this.id, winnerName, score, winnerName + " da best!"));
        this.playerTwo.sendOutbound(ServerMessageBuilder.gameFinished(this.id, winnerName, score, winnerName + " da best!"));

        this.playerOne.sendOutbound(ServerMessageBuilder.requestRematch());
        this.playerTwo.sendOutbound(ServerMessageBuilder.requestRematch());
    }

    /**
     * Called when a player is removed from the game for any reason.
     * @param player ClientHandler object to be removed from the game
     */
    void removePlayer(ClientHandler player) {
        ClientHandler disconnectedPlayer = player == this.playerOne ? this.playerOne : this.playerTwo;
        ClientHandler remainingPlayer = player == this.playerOne ? this.playerTwo : this.playerOne;

        if (this.gameState != GameState.WAITING) {
            // self destruct
            Map<TileColour, Double> defaultScore = new HashMap<>();
            defaultScore.put(this.playerRoles.get(remainingPlayer), 1000.0);
            defaultScore.put(this.playerRoles.get(disconnectedPlayer), 0.0);

            remainingPlayer.sendOutbound(ServerMessageBuilder.gameFinished(
                    this.id,
                    remainingPlayer.username(),
                    defaultScore,
                    disconnectedPlayer.username() + "disconnected. "
                            + remainingPlayer.username() + " wins by default."
            ));

            this.gameManager.endGame(this);
        } else {
            if (this.playerOne == disconnectedPlayer) {
                this.playerOne = null;
                return;
            }

            this.playerTwo = null;
        }
    }

    boolean isFull() {
        return this.playerOne != null && this.playerTwo != null;
    }

    List<ClientHandler> getPlayers() {
        return Arrays.asList(playerOne, playerTwo);
    }

    int id() {
        return this.id;
    }

    public Board board() {
        return this.board;
    }

    public GameState gameState() {
        return this.gameState;
    }
}
