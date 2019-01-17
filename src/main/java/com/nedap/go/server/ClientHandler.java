package com.nedap.go.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Server server;
    private BufferedReader inStream;
    private BufferedWriter outStream;

    public ClientHandler(Server server, Socket socket) {
        this.server = server;

        try {
            this.inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.outStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            // TODO: Notify server that this client is toast.
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            String inbound;
            while ((inbound = inStream.readLine()) != null) {
                Logger.log(inbound);
                // TODO: Handle message
            }
        } catch (IOException e) {
            // TODO: Notify server that this handler is toast.
        }
    };
}
