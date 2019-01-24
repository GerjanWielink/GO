package com.nedap.go.server;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.fail;

public class ServerTest {
    private static final int PORT = 8008;

    private Server server;

    @Before
    public void setup() {
        this.server = new Server(PORT);
    }

    @Test
    public void shouldStartAndShutdown() {
        try {
            server.start();

            Thread.sleep(5000);

            server.kill();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void shouldStartAcceptConnectionsAndShutdown() {
        server.start();

        try {
            for (int i = 0; i < 100; i++) {
                Socket serverSocket = new Socket("127.0.0.1", PORT);
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }

        server.kill();
    }

    @Test
    public void shouldAcceptConnectionsAndIdentifications() {
        server.start();


        try {
            for (int i = 0; i < 100; i ++) {
                // Not the socket that should be in the ClientHandler, results in sending data to itself. Works for testing purposes
                ClientHandler handler = new ClientHandler(server, new Socket("127.0.0.1", PORT));

                Thread.sleep(50);

                handler.handleHandshakeCommand("Player" + (int) (Math.random() * 1000));
            }
        } catch (IOException | InterruptedException e) {
            fail(e.getMessage());
        }
    }
}
