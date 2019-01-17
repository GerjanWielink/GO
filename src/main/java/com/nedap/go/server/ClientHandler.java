package com.nedap.go.server;

import com.nedap.go.protocol.ServerMessageBuilder;
import com.nedap.go.server.exceptions.YouAreNotTheCommanderException;
import com.nedap.go.utilities.TileColour;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Server server;
    private Socket socket;
    private String username;
    private GameInstance game;
    private boolean isGameLeader;
    private BufferedReader inStream;
    private BufferedWriter outStream;
    private CommandRouter commandRouter;
    private int consecutiveUnknownCommands = 0;

    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        this.commandRouter = new CommandRouter(this);
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
                this.commandRouter.route(inbound);
            }
        } catch (IOException e) {
            // TODO: Notify server that this handler is toast.
        }
    };

    public void resetUnknownCommandCount() {
        this.consecutiveUnknownCommands = 0;
    }

    public void handleUnknownCommand() {
        this.consecutiveUnknownCommands++;

        if(this.consecutiveUnknownCommands > 10) {
            try {
                this.disconnect();
            } catch (IOException e) {
                Logger.error("IOException closing socket connection");
            }
        }
    }

    public void handleHandshakeCommand(String name) {
        if (this.username != null) {
            this.handleUnknownCommand();
            return;
        }

        this.resetUnknownCommandCount();

        this.username = name;
        this.server.identify(this);
    }

    public void handleSetConfigCommand(TileColour preferredColour, int boardSize) {
        if (!this.isGameLeader) {
            // TODO
            return;
        }

        this.game.provideConfig(preferredColour, boardSize);
    }

    public void notifyGameStart() {

    }

    public synchronized void addToGame(GameInstance gameInstance, boolean isGameLeader) {
        this.game = gameInstance;
        this.isGameLeader = isGameLeader;

        sendOutbound(ServerMessageBuilder.acknowledgeHandshake(this.game.id(), this.isGameLeader));

        if(this.isGameLeader) {
            sendOutbound(ServerMessageBuilder.requestConfig());
        }
    }


    public String username() {
        return this.username;
    }


    public void sendOutbound(String message) {
        try {
            this.outStream.write(message);
            this.outStream.newLine();
            this.outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() throws IOException {
        try {
            this.inStream.close();
            this.outStream.close();
        }

        finally {
            this.socket.close();
            // TODO: Remove from game instance
            String username = this.username != null ? this.username : "anoymous";
            Logger.error("Client \"" + username +  "\" kicked from server..");
        }
    }
}
