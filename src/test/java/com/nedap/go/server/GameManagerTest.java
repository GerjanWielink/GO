package com.nedap.go.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.fail;

public class GameManagerTest {
    public static final int PORT = 8001;

    Server server;
    GameManager gameManager;
    List<ClientHandler> mockClients;

    @Before
    public void setup() {
        this.server = new Server(PORT);
        this.mockClients = new ArrayList<>();

        server.start();

        try {
            for (int i = 0; i < 5; i++) {
                mockClients.add(new ClientHandler(this.server, new Socket("127.0.0.1", PORT)));
                Thread.sleep(200);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        this.gameManager = new GameManager(this.server);
    }

    @Test
    public void shouldAcceptMultipleClients () {
        try {
            mockClients.forEach(client -> {
                this.gameManager.addPlayer(client);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldAcceptMultipleHandshakes () {
        try {
            mockClients.forEach(client -> this.gameManager.addPlayer(client));
            mockClients.forEach(client -> client.handleHandshakeCommand("Player" + (int) (Math.random() * 1000)));

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @After
    public void kill() {
        this.server.kill();
    }
}
