package com.nedap.go.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


class InboundMessageHandler extends  Thread {
    private Socket socket;
    private ClientHandler clientHandler;

    public InboundMessageHandler (Socket socket, ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.socket = socket;
    }

    public void run () {
        String inbound;

        try (BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            while ((inbound = inStream.readLine()) != null) {
                this.clientHandler.handleCommand(inbound);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
