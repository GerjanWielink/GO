package com.nedap.go.client;

import com.nedap.go.client.exceptions.InvalidInputException;
import com.nedap.go.client.validators.*;
import com.nedap.go.protocol.ClientCommandBuilder;
import com.nedap.go.server.Logger;
import com.nedap.go.utilities.TileColour;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ClientHandler {
    private String username;
    private InetAddress host;
    private GameManager gameManager;
    private int gameId;
    private BufferedWriter outStream;
    private int port;
    private CommandRouter commandRouter;

    public static void main (String[] args) {
        ClientHandler handler = new ClientHandler();

        handler.connect();
        handler.promptUsername();
    }

    public ClientHandler() {
        this.commandRouter = new CommandRouter(this);
    }

    public void connect() {
        this.promptHostName();
        this.promptPort();

        try {
            Socket gameSocket = new Socket(this.host, this.port);
            this.outStream = new BufferedWriter(new OutputStreamWriter(gameSocket.getOutputStream()));
            (new InboundMessageHandler(gameSocket, this)).start();
            Logger.log("Connected to  " + this.host + ":" + this.port);
        } catch (IOException e) {
            Logger.log("Error finding host. Please try again.");
            this.connect();
        }
    }

    public void handleCommand(String message) {
        commandRouter.route(message);
    }

    public void reConnect() {
        try {
            Socket gameSocket = new Socket(this.host, this.port);
            this.outStream = new BufferedWriter(new OutputStreamWriter(gameSocket.getOutputStream()));
            (new InboundMessageHandler(gameSocket, this)).start();
            Logger.log("Connected to " + this.host + ":" + this.port);
            this.doHandshake();
        } catch (IOException e) {
            //
        }

    }

    public void promptHostName() {
        this.host = (InetAddress) promptInput(
                "Please provide the host address (localhost): ",
                new HostValidator()
        );
    }

    public void handleAcknowledgeConfig(String name, TileColour colour, int boardSize, String state) {
        if (!this.username.equals(name)) {
            Logger.log("Username changed by server to " + name);
            this.username = name;
        }

        this.gameManager = new GameManager(boardSize, colour, state, this);
    }

    public void handleAcknowledgeMove(String move, String gameState) {
        this.gameManager.update(move, gameState);
    }

    public void promptPort() {
        this.port = (int) promptInput(
                "Please provide the server port(8000): ",
                new PortValidator()
        );
    }

    public void promptUsername() {
        this.username = (String) promptInput(
                "Please provide your username: ",
                new UsernameValidator()
        );

        this.doHandshake();
    }

    public void doHandshake() {
        if (this.username == null) {
            this.promptUsername();
        }

        this.sendOutBound(ClientCommandBuilder.handshake(this.username));
    }

    public void handleUnkownCommand(String message) {
        this.gameManager.displayMessage("Unknown server message: " + message);
    }

    public void handleAcknowledgeHandshake(int gameId) {
        this.gameId = gameId;
    }

    public void handleInvalidMove(String message) {
        this.gameManager.displayMessage("Invalid move: " + message);
    }

    public void handleGameFinished() {}

    public void handleUpdateStatus() {}

    public void handleRequestConfig() {
        TileColour preferredColour = (TileColour) promptInput(
                "What colour would you like to play (b/w)?",
                new ColourValidator()
        );

        int boardSize = (int) promptInput(
                "What board size would you like to play (5 - 19)?",
                new BoardSizeValidator()
        );

        sendOutBound(ClientCommandBuilder.setConfig(
                this.gameId,
                preferredColour,
                boardSize
        ));
    }

    public void sendOutBound(String message) {
        try {
            this.outStream.write(message);
            this.outStream.newLine();
            this.outStream.flush();
        } catch (IOException e) {
            Logger.error("IOException in ClientHandler::sendOutBound");
        }
    }

    public int gameId() {
        return gameId;
    }

    public String username() {
        return username;
    }

    static private Object promptInput(String message, CommandInputValidator validator) {
        System.out.print(message);
        String response = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            response = in.readLine();

            return validator.validate(response);

        } catch (IOException | InvalidInputException e) {
            return promptInput(message, validator);
        }
    }
}
