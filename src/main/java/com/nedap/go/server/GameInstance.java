package com.nedap.go.server;

import com.nedap.go.server.exceptions.GameFullException;
import com.nedap.go.utilities.Board;

public class GameInstance {
    private static final int NUM_PLAYERS = 2;

    private int id;
    private GameState gameState;
    private ClientHandler playerBlack;
    private ClientHandler playerWhite;
    private Board board;

    public GameInstance(int id) {
        this.id = id;
        this.gameState = GameState.AWAITING_PLAYER_1;
    }

    public GameState gameState() {
        return this.gameState;
    }

    public void addPlayer(ClientHandler player) throws GameFullException {
        if(this.playerBlack == null) {
            this.playerBlack = player;
            return;
        }

        if(this.playerWhite == null) {
            this.playerWhite = player;
            return;
        }

        throw new GameFullException();
    }

}
