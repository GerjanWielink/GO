package com.nedap.go.client.validators;

import com.nedap.go.client.exceptions.InvalidInputException;

public class PortValidator implements CommandInputValidator {

    @Override
    public Object validate(String message) throws InvalidInputException {
        try {
            if(message.equals("")){
                return 8000;
            }

            return Integer.parseInt(message, 10);
        } catch (NumberFormatException e) {
            throw new InvalidInputException();
        }
    }
}