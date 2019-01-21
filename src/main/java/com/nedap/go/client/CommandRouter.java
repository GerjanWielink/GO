package com.nedap.go.client;

import com.nedap.go.protocol.ServerCommand;
import com.nedap.go.server.Logger;
import com.nedap.go.utilities.TileColour;

public class CommandRouter {
    private ClientHandler gameHandler;

    public CommandRouter(ClientHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    public void route (String message) {
        ServerCommand command = getCommandFromMessage(message);

        if (command == null) {
            // TODO
            Logger.log("Unknow command: " + message);
            return;
        }

        switch (command) {
            case REQUEST_CONFIG:
                this.handleRequestConfig(message);
                break;

            case ACKNOWLEDGE_HANDSHAKE:
                this.handleAcknowledgeHandshake(message);
                break;

            case ACKNOWLEDGE_CONFIG:
                this.handleAcknowledgeConfig(message);
                break;

            case ACKNOWLEDGE_MOVE:
                this.handleAcknowledgeMove(message);
                break;

            default:
                Logger.log("Unknow command: " + message);
        }
    }

    private void handleAcknowledgeHandshake(String message) {
        String[] tokens = message.split("\\+");

        if (tokens.length != 3) {
            // TODO: handleInvalidCommand
            return;
        }

        try {
            int gameId = Integer.parseInt(tokens[1], 10);
            boolean isLeader = tokens[2].equals("1");

            this.gameHandler.handleAcknowledgeHandshake(gameId, isLeader);
        } catch (NumberFormatException e ) {
            // TODO: handleInvalidCommand
            return;
        }

    }

    private void handleAcknowledgeMove(String message) {
        String[] tokens = message.split("\\+");

        if(tokens.length != 4) {
            // TODO: handleInvalidCommand
            return;
        }

        String gameState = tokens[3];

        this.gameHandler.handleAcknowledgeMove(gameState);
    }


    private void handleAcknowledgeConfig(String message) {
        String[] tokens = message.split("\\+");

        if(tokens.length != 5) {
            // TODO: handleInvalidCommand
            return;
        }

        String name = tokens[1];
        TileColour colour = tokens[2].equals("2") ? TileColour.WHITE : TileColour.BLACK;
        int size = Integer.parseInt(tokens[3],10);
        String gameState = tokens[4];


        this.gameHandler.handleAcknowledgeConfig(name, colour, size, gameState);
    }

    private void handleRequestConfig(String message) {
        this.gameHandler.handleRequestConfig();
    }


    private ServerCommand getCommandFromMessage (String message) {
        String commandString = message.split("\\+")[0];

        for(ServerCommand command: ServerCommand.values()){
            if (command.toString().equals(commandString)) {
                return command;
            }
        }

        return null;
    }
}
