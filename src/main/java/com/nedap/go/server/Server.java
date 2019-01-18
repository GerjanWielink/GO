package com.nedap.go.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private boolean run;
    private List<ClientHandler> unidentifiedConnections;
    private GameManager gameManager;

    public Server(int port){
        this.port = port;
        this.unidentifiedConnections = new ArrayList<>();
        this.gameManager = new GameManager(this);
    }

    public static void main (String[] args) throws IOException {
        Server hi = new Server(8000);

        hi.start();
    }

    public void start() throws IOException {
        this.run = true;

        ServerSocket serverSocket = new ServerSocket(port);
        Logger.log("Server listening for connections on port " + port);

        serverSocket.setSoTimeout(200);

        while (run) {
            Logger.log("LISTENING!!!");
            try {
                Socket clientSocket = serverSocket.accept();
                Logger.log("Anonymous client connected");

                ClientHandler newHandler = new ClientHandler(this, clientSocket);
                newHandler.start();

                this.unidentifiedConnections.add(newHandler);
            } catch (SocketTimeoutException e) {
                //
            }
        }
    }

    public void stop() {
        this.run = false;
    }

    public void identify(ClientHandler client) {
        String username = client.username();

        if (username != null) {
            this.unidentifiedConnections.remove(client);
            Logger.log("Client " + username + " identified");
            this.gameManager.addPlayer(client);
        }
    }
}
