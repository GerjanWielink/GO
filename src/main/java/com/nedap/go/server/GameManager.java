package com.nedap.go.server;

import java.util.ArrayList;

public class GameManager {
    private Server server;
    private int currentId;
    private ArrayList<ClientHandler> playerQueue;
    private ArrayList<GameInstance> gameInstances;

    public GameManager(Server server) {
        this.currentId = 0;
        this.server = server;
        this.playerQueue = new ArrayList<>();
        this.gameInstances = new ArrayList<>();
        this.gameInstances.add(new GameInstance(this.generateId()));
    }

    public void addPlayer(ClientHandler newClient) {
        this.playerQueue.add(newClient);
    }

    private int generateId() {
        this.currentId++;
        return this.currentId;
    }
}
