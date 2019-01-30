package com.nedap.go.server;

import com.nedap.go.utilities.IO;
import com.nedap.go.utilities.Logger;
import com.nedap.go.utilities.validators.PortValidator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private Integer port;
    private List<ClientHandler> connections;
    private GameManager gameManager;
    private ServerSocket serverSocket;

    public Server() {
        this.connections = new ArrayList<>();
        this.gameManager = new GameManager(this);
    }

    public Server(Integer port) {
        this.port = port;
        this.connections = new ArrayList<>();
        this.gameManager = new GameManager(this);
    }

    public static void main (String[] args) throws IOException {
        (new Server()).start();
    }

    /**
     * Main body of the server Thread. Responsible for opening the socket and listening for connections.
     */
    public void run() {
        this.openSocket();

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

    private void openSocket() {
        try {
            if (this.port == null) {
                this.port = (int) IO.promptInput("Please provide a socket for the server to listen on (8000):", new PortValidator());
            }
            this.serverSocket = new ServerSocket(port);
            Logger.log("Server listening for connections on port " + port);
        } catch (IOException | IllegalArgumentException e) {
            Logger.error("Error opening socket on port: " + this.port+ ". Port is not available or out of range. Please provide another port.");
            this.port = null;
            this.openSocket();
            return;
        }
    }

    /**
     * Kills the server by interrupting the thread.
     */
    public void kill() {
        try {
            this.serverSocket.close();
            this.interrupt();
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
    }

    /**
     * Identify a client to the server after a handshake is received. Changes the client from an
     * anonymous player to a proper GO player.
     * @param client the ClientHandler object which sent a handshake.
     */
    public void identify(ClientHandler client) {
        String username = client.username();

        if (username != null) {
            Logger.log("ClientHandler " + username + " identified");
            this.gameManager.addPlayer(client);
        }
    }
}
