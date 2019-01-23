package com.nedap.go.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;


public class ClientHandlerTest {
    private Server server;
    private ClientHandler clientHandler;
    private static final int PORT = 8008;

    @Before
    public void setup() {
        try {
            this.server = new Server(PORT);
            this.server.start();

            this.clientHandler = new ClientHandler(this.server, new Socket("127.0.0.1", PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldSendOutbound() {

    }

    @After
    public void kill() {
        this.server.kill();
    }
}
