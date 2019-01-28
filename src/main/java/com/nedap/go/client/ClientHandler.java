package com.nedap.go.client;

import com.nedap.go.utilities.validators.*;
import com.nedap.go.protocol.ClientCommandBuilder;
import com.nedap.go.server.Logger;
import com.nedap.go.utilities.IO;
import com.nedap.go.utilities.TileColour;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ClientHandler {
    private String username;
    private String opponentUsername;
    private InetAddress host;
    private GameManager gameManager;
    private BufferedWriter outStream;
    private CommandRouter commandRouter;

    private boolean fresh = true;
    private int gameId;
    private int port;
    private boolean computerPlayer;
    private int maxMoveTime;

    public static void main (String[] args) {
        ClientHandler handler = new ClientHandler();

        handler.connect();

        handler.promptAi();
        handler.promptMaxMoveTime();
        handler.promptUsername();
    }

    public ClientHandler() {
        this.commandRouter = new CommandRouter(this);
    }

    public void connect() {
        this.promptHostName();
        this.promptPort();

        this.doConnect();
    }

    public void handleCommand(String message) {
        commandRouter.route(message);
    }

    public void reConnect() {
        this.doConnect();
        this.doHandshake();
    }

    private void doConnect() {
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

    public void promptHostName() {
        this.host = (InetAddress) IO.promptInput(
                "Please provide the host address (localhost): ",
                new HostValidator()
        );
    }

    public void promptMaxMoveTime() {
        this.maxMoveTime = (int) IO.promptInput(
            "Please provide the maximum move time for the computer player: ",
            new MoveTimeValidator()
        );
    }

    public void handleAcknowledgeConfig(String name, TileColour colour, int boardSize, String state, String opponentUsername) {
        if (this.fresh) {
            if (!this.username.equals(name)) {
                Logger.log("Username changed by server to " + name);
                this.username = name;
                this.opponentUsername = opponentUsername;
            }
            this.fresh = false;
            this.gameManager = new GameManager(boardSize, colour, state, this, this.computerPlayer, this.maxMoveTime, this.opponentUsername);
        } else {
            this.gameManager.update(null, state);
        }
    }

    public void handleRequestRematch() {
        boolean rematch = (boolean) IO.promptInput(
                "Would you like a rematch (y/n)?",
                new BooleanValidator()
        );

        this.sendOutBound(ClientCommandBuilder.setRematch(rematch));
    }

    public void handleAcknowledgeRematch(boolean rematch) {
        if (rematch) {
            this.gameManager.displayMessage("Opponent agreed to a rematch.");
        } else {
            this.gameManager.displayMessage("Opponent does not want a rematch.");
        }
    }

    public void handleGameFinished(String winner, String score) {
        String black = score.split(";")[0];
        String white = score.split(";")[1];

        this.gameManager.displayMessage("Game finished. Black: "
                + black + " White: " + white +
                ". " + winner + " wins!");
    }

    public void handleAcknowledgeMove(String move, String gameState) {
        this.gameManager.update(move, gameState);
    }

    public void promptPort() {
        this.port = (int) IO.promptInput(
                "Please provide the server port(8000): ",
                new PortValidator()
        );
    }

    public void promptUsername() {
        this.username = (String) IO.promptInput(
                "Please provide your username: ",
                new UsernameValidator()
        );

        this.doHandshake();
    }

    public void promptAi() {
        this.computerPlayer = (boolean) IO.promptInput(
                "Do you want the computer to play for you? (y/n)",
                new BooleanValidator()
        );
    }

    public void doHandshake() {
        if (this.username == null) {
            this.promptUsername();
        }

        this.sendOutBound(ClientCommandBuilder.handshake(this.username));
    }

    public void handleUnknownCommand(String message) {
        this.gameManager.displayMessage("Unknown server message: " + message);
    }

    public void handleAcknowledgeHandshake(int gameId) {
        this.gameId = gameId;
    }

    public void handleInvalidMove(String message) {
        this.gameManager.displayMessage("Invalid move: " + message);
    }

    public void handleRequestConfig() {
        TileColour preferredColour = (TileColour) IO.promptInput(
                "What colour would you like to play (b/w)?",
                new ColourValidator()
        );

        int boardSize = (int) IO.promptInput(
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
}
