package com.nedap.go.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;
    private GameManager gameManager;

    public Server(int port){
        this.port = port;
        this.gameManager = new GameManager(this);
    }

    public static void main (String[] args) throws IOException {
        Server hi = new Server(8000);
        hi.start();
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        Logger.log("Server listening for connections on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            Logger.log("Client connected");

            ClientHandler newHandler = new ClientHandler(this, clientSocket);
            newHandler.start();

            this.gameManager.addPlayer(newHandler);
        }
    }

}
