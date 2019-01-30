package com.nedap.go.server;

import com.nedap.go.server.exceptions.GameFullException;
import com.nedap.go.utilities.Logger;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Responsible for distributing player connections over game instances.
 */
public class GameManager {
    private Server server;
    private int currentId;
    private ArrayList<GameInstance> gameInstances;

    public GameManager(Server server) {
        this.currentId = 0;
        this.server = server;
        this.gameInstances = new ArrayList<>();
        this.gameInstances.add(new GameInstance(this.generateId(), this));
    }

    public synchronized void addPlayer(ClientHandler client) {
        if (gameInstances.size() == 0) {
            gameInstances.add(new GameInstance(this.generateId(), this));
        }

        GameInstance endOfQueueGame = gameInstances.get(gameInstances.size() - 1);

        try {
            // Game is full or absent
            if (endOfQueueGame == null || endOfQueueGame.isFull()) {
                this.gameInstances.add(new GameInstance(this.generateId(), this, client));
                return;
            }

            endOfQueueGame.addPlayer(client);
        } catch (GameFullException e) {
            this.gameInstances.add(new GameInstance(this.generateId(), this, client));
        }
    }

    public synchronized void endGame(GameInstance gameInstance) {
        this.gameInstances.remove(gameInstance);
        gameInstance.getPlayers().forEach(clientHandler -> {
            try {
                clientHandler.disconnect();
            } catch (IOException e) {
                Logger.error("IOException in GameManager::endGame");
            }
        });
    }

    private int generateId() {
        this.currentId++;
        return this.currentId;
    }
}
