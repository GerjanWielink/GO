package com.nedap.go.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ClientHandlerTest {
    private Server server;
    private List<ClientHandler> clientHandlers;
    private static final int PORT = 8001;

    @Before
    public void setup() {
        try {
            this.server = new Server(8001);

            for(int i = 0; i < 5; i++) {
                clientHandlers.add(new ClientHandler(server, new Socket("127.0.0.1", PORT)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
    }

    @After
    public void kill() {
        this.server.stop();
    }
}
