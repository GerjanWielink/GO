package com.nedap.go.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private int port;
    private List<ClientHandler> connections;
    private GameManager gameManager;
    private ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
        this.connections = new ArrayList<>();
        this.gameManager = new GameManager(this);
    }

    public static void main (String[] args) throws IOException {
        Server hi = new Server(8000);
        hi.start();
    }

    public void run() {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.log("Server listening for connections on port " + port);

        while (!Thread.currentThread().isInterrupted()) {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
                Logger.log("Anonymous client connected");

                ClientHandler newHandler = new ClientHandler(this, clientSocket);
                newHandler.start();

                this.connections.add(newHandler);
            } catch (IOException e) {
                if (e instanceof SocketException) {
                    Logger.log("Server socket closed");
                } else {
                    Logger.error("IOException in Server::run()");
                }
            }
        }
    }

    public void kill() {
        try {
            this.serverSocket.close();
            this.interrupt();
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
    }

    public void identify(ClientHandler client) {
        String username = client.username();

        if (username != null) {
            Logger.log("ClientHandler " + username + " identified");
            this.gameManager.addPlayer(client);
        }
    }
}
