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
        handler.start();
    }

    public ClientHandler() {
        this.commandRouter = new CommandRouter(this);
    }

    /**
     * Start the client handler. Prompt dead required settings.
     */
    public void start() {
        this.connect();
        this.promptAi();
        this.promptMaxMoveTime();
        this.promptUsername();
    }

    /**
     * Prompt the server details and attempt to connect
     */
    public void connect() {
        try {
            this.promptHostName();
            this.promptPort();
            this.doConnect();
        } catch (IOException e) {
            Logger.error("Error connecting to host. Please provide new server details.");
            this.connect();
        }
    }

    /**
     * Passes incoming server commands to the
     * command router.
     * @param message raw message received from the server
     */
    public void handleCommand(String message) {
        commandRouter.route(message);
    }

    public void reConnect() {
        try {
            this.doConnect();
            this.doHandshake();
        } catch (IOException e) {
            Logger.error("Error connecting to host. Please provide new server details");
            this.connect();
        }
    }

    /**
     * Actually try to connect to the server
     */
    private void doConnect() throws IOException {
        Socket gameSocket = new Socket(this.host, this.port);
        this.outStream = new BufferedWriter(new OutputStreamWriter(gameSocket.getOutputStream()));
        (new InboundMessageHandler(gameSocket, this)).start();
        Logger.log("Connected to  " + this.host + ":" + this.port);
    }


    public void handleAcknowledgeConfig(String name, TileColour colour, int boardSize, String state, String opponentUsername) {
        /**
         * The "fresh" flag is set to false on the first game started
         * After this we handle it as a reset and we do not need to restart the GUI etc.
         */
        if (this.fresh) {
            /**
             * indicates that the requested username has been changed by the server.
             * Might be due to it already being taken. We don't actually do to much with
             * this but let's log it anyways.
             */
            if (!this.username.equals(name)) {
                Logger.log("Username changed by server to " + name);
                this.username = name;
                this.opponentUsername = opponentUsername;
            }
            this.fresh = false;
            this.gameManager = new GameManager(boardSize, colour, state, this, this.computerPlayer, this.maxMoveTime, this.opponentUsername);
        } else {
            this.gameManager.resetGame(state);
        }
    }

    /**
     * Handles the REQUEST_REMATCH command. Prompts the user
     * for a rematch
     */
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

    /**
     * Hand the REQUEST_CONFIG command. Prompts the preferred colour and board size and
     * returns the desired settings to the server.
     */
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

    /**
     * Send a message to the server.
     * @param message Message to be sent to the server.
     */
    public void sendOutBound(String message) {
        Logger.outbound(message);
        try {
            this.outStream.write(message);
            this.outStream.newLine();
            this.outStream.flush();
        } catch (IOException e) {
            Logger.error("IOException in ClientHandler::sendOutBound");
        }
    }



    /**
     * Prompt the desired username.
     */
    private void promptUsername() {
        this.username = (String) IO.promptInput(
                "Please provide your username: ",
                new UsernameValidator()
        );

        this.doHandshake();
    }

    /**
     * Prompt if the client would like a computer player.
     */
    public void promptAi() {
        this.computerPlayer = (boolean) IO.promptInput(
                "Do you want the computer to play for you? (y/n)",
                new BooleanValidator()
        );
    }

    /**
     * Prompt the address of the server.
     */
    private void promptHostName() {
        this.host = (InetAddress) IO.promptInput(
                "Please provide the host address (localhost): ",
                new HostValidator()
        );
    }

    /**
     * Prompt the port for the game  server.
     */
    private void promptPort() {
        this.port = (int) IO.promptInput(
                "Please provide the server port(8000): ",
                new PortValidator()
        );
    }

    /**
     * Prompt the max move time in ms.
     */
    private void promptMaxMoveTime() {
        this.maxMoveTime = (int) IO.promptInput(
                "Please provide the maximum move time for the computer player: ",
                new MoveTimeValidator()
        );
    }


    /**
     * Get the gameId
     * @return id of the game instance.
     */
    public int gameId() {
        return gameId;
    }

    /**
     * Get the username
     * @return username of the client.
     */
    public String username() {
        return username;
    }
}
