package com.nedap.go.server;

import com.nedap.go.protocol.ClientCommand;
import com.nedap.go.utilities.TileColour;
public class CommandRouter {
    private ClientHandler handler;

    public static void main (String[] args) {
        String test = "HANDSHAKE+HOIDOEI+NEE+HAHA";
        System.out.println(test.split("\\+", 2)[1]);
    }

    public CommandRouter(ClientHandler handler) {
        this.handler = handler;
    }

    public void route(String message) {
        ClientCommand command = getCommandFromMessage(message);

        if (command == null) {
            handler.handleUnknownCommand();
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

            default:
                handler.handleUnknownCommand();
        }
    }

    // HANDSHAKE+$USERNAME
    private void handleHandshakeCommand(String message) {
        String[] tokens = message.split("\\+", 2);
        if (tokens.length != 2) {
            handler.handleUnknownCommand();
            return;
        }

        String username = tokens[1];

        handler.handleHandshakeCommand(username);
    }

    // SET_CONFIG+$GAME_ID+$PREFERRED_COLO+$BOARD_SIZE
    private void handleSetConfigCommand(String message) {
        String[] tokens = message.split("\\+", 4);

        TileColour colour = TileColour.BLACK;
        int boardSize = 19;

        if (tokens.length < 2) { // no game_id
            handler.handleUnknownCommand();
            return;
        }

        if (tokens.length > 2) { // preferred color
            colour = tokens[2] == "2" ? TileColour.WHITE : TileColour.BLACK;
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

    // MOVE+$GAME_ID+$PLAYER+$TILE_INDEX
    private void handleMoveCommand(String message) {
        String[] tokens = message.split("\\+");

        if (tokens.length != 4) {
            handler.handleUnknownCommand();
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
