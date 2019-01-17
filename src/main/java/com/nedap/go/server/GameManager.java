package com.nedap.go.server;

import com.nedap.go.server.exceptions.GameFullException;

import java.util.ArrayList;

public class GameManager {
    private Server server;
    private int currentId;
    private ArrayList<GameInstance> gameInstances;

    public GameManager(Server server) {
        this.currentId = 0;
        this.server = server;
        this.gameInstances = new ArrayList<>();
        this.gameInstances.add(new GameInstance(this.generateId()));
    }

    public synchronized void addPlayer(ClientHandler client) {
        GameInstance endOfQueueGame = gameInstances.get(gameInstances.size() - 1);

        try {
            // Game is full or absent
            if (endOfQueueGame == null || endOfQueueGame.isFull()) {
                this.gameInstances.add(new GameInstance(this.generateId(), client));
                return;
            }

            endOfQueueGame.addPlayer(client);
        } catch (GameFullException e) {
            this.gameInstances.add(new GameInstance(this.generateId(), client));
        }
    }

    private int generateId() {
        this.currentId++;
        return this.currentId;
    }
}
