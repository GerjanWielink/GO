package com.nedap.go.client;

import com.nedap.go.server.Server;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class ClientHandlerTest {
    private ClientHandler handler;
    private Server server;
    private InputStream instream;

    private static final int PORT = 8080;

    @Before
    public void setup() {


        System.setIn(this.instream);

        this.server = new Server(PORT);
        this.server.start();

        this.handler = new ClientHandler();
    }

    @Test
    public void shouldStartTheClientHandler() {
//        this.handler.start();
    }
}
