package com.nedap.go.server;

import com.nedap.go.protocol.ClientCommand;
import com.nedap.go.utilities.Logger;
import com.nedap.go.utilities.TileColour;

import java.io.IOException;


public class CommandRouter {
    private ClientHandler handler;

    public CommandRouter(ClientHandler handler) {
        this.handler = handler;
    }

    /**
     * Maps commands to their respective handler
     * @param message provided command
     */
    public void route(String message) {
        Logger.inbound(message);
        ClientCommand command = getCommandFromMessage(message);

        if (command == null) {
            handler.handleUnknownCommand(message);
            return;
        }

        switch (command) {
            case HANDSHAKE:
                this.handleHandshakeCommand(message);
                break;

            case SET_CONFIG:
                this.handleSetConfigCommand(message);
                break;

            case MOVE:
                this.handleMoveCommand(message);
                break;

            case PASS:
                this.handlePassCommand(message);
                break;

            case SET_REMATCH:
                this.handleAcknowledgeRematch(message);
                break;

            case EXIT:
                this.handleExitCommand(message);
                break;

            default:
                handler.handleUnknownCommand(message);
        }
    }


    /**
     * HANDSHAKE+$USERNAME
     * @param message provided command
     */
    private void handleHandshakeCommand(String message) {
        String[] tokens = message.split("\\+");
        if (tokens.length != 2) {
            handler.handleUnknownCommand("Error parsing command. Invalid token length. Remember that usernames cannot contain \"+\" signs");
            return;
        }

        String username = tokens[1];

        handler.handleHandshakeCommand(username);
    }


    /**
     * SET_CONFIG+$GAME_ID+$PREFERRED_COLO+$BOARD_SIZE
     * @param message provided command
     */
    private void handleSetConfigCommand(String message) {
        String[] tokens = message.split("\\+");

        TileColour colour = TileColour.BLACK;
        int boardSize = 19;

        if (tokens.length < 2 || tokens.length > 4) { // no game_id or too many params
            handler.handleUnknownCommand("Error parsing command. Invalid token length.");
            return;
        }

        if (tokens.length > 2) { // preferred color
            colour = tokens[2].equals("2") ? TileColour.WHITE : TileColour.BLACK;
        }

        if (tokens.length > 3) {
            try {
                boardSize = Integer.parseInt(tokens[3], 10);
            } catch (NumberFormatException e) {
                // just go for default..
            }
        }

        handler.handleSetConfigCommand(colour, boardSize);
    }

    /**
     * SET_REMATCH+$REMATCH
     * @param message provided command
     */
    private void handleAcknowledgeRematch(String message) {
        String[] tokens = message.split("\\+");
        if(tokens.length != 2) {
            handler.handleUnknownCommand("Error parsing command. Invalid token length");
            return;
        }

        boolean rematch = tokens[1].equals("1");
        handler.handleRematch(rematch);
    }

    /**
     * MOVE+$GAME_ID+$PLAYER+$TILE_INDEX
     * @param message provided command
     */
    private void handleMoveCommand(String message) {
        String[] tokens = message.split("\\+");

        if (tokens.length != 4) {
            handler.handleUnknownCommand("Error parsing command. Invalid token length.");
            return;
        }

        // TODO: Verification on gameId and playername (redundant but meh)

        String playerName = tokens[2];

        try {
            int gameId = Integer.parseInt(tokens[1], 10);
            int index = Integer.parseInt(tokens[3], 10);

            if (index == -1) {
                handler.handlePassCommand();
                return;
            }


            handler.handleMoveCommand(index);
        } catch (NumberFormatException e) {
            handler.handleUnknownCommand("Error parsing command. Argument provided is not a valid integer.");
        }

    }

    /**
     * Pass+$GAME_ID+$PLAYER_NAME
     * @param message provided command
     */
    private void handlePassCommand(String message) {
        String[] tokens = message.split("\\+");

        if(tokens.length != 3) {
            handler.handleUnknownCommand("Error parsing command. Invalid token length.");
            return;
        }

        handler.handlePassCommand();
    }

    private void handleExitCommand(String message) {
        try {
            handler.disconnect();
        } catch (IOException e) {
            //
        }
    }

    private ClientCommand getCommandFromMessage (String message) {
        String commandString = message.split("\\+")[0];

        for(ClientCommand command: ClientCommand.values()){
            if (command.toString().equals(commandString)) {
                return command;
            }
        }

        return null;
    }
}
