package com.nedap.go.utilities.validators;

import com.nedap.go.utilities.Logger;
import com.nedap.go.utilities.exceptions.InvalidInputException;

public class PortValidator implements CommandInputValidator {

    @Override
    public Object validate(String message) throws InvalidInputException {
        try {
            if(message.equals("")){
                return 8000;
            }

            return Integer.parseInt(message, 10);
        } catch (NumberFormatException e) {
            Logger.error("Error parsing port. Please provide a valid integer.");
            throw new InvalidInputException();
        }
    }
}