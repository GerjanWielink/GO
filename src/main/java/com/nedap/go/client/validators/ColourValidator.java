package com.nedap.go.client.validators;

import com.nedap.go.client.exceptions.InvalidInputException;
import com.nedap.go.utilities.TileColour;

public class ColourValidator implements CommandInputValidator {
    @Override
    public Object validate(String message) throws InvalidInputException {
        if(message.equals("w") || message.equals("2") || message.equals("white")) {
            return TileColour.WHITE;
        }

        if(message.equals("b") || message.equals("1") || message.equals("black") || message.equals("")) {
            return TileColour.BLACK;
        }

        throw new InvalidInputException();
    }
}
