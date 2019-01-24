package com.nedap.go.client.validators;

import com.nedap.go.client.exceptions.InvalidInputException;

public class BooleanValidator implements CommandInputValidator {

    @Override
    public Object validate(String message) throws InvalidInputException {
        if(message.equals("y") || message.equals("yes") || message.equals("1") || message.equals("ja")) {
            return true;
        }

        if(message.equals("n") || message.equals("no") || message.equals("0") || message.equals("nee")) {
            return false;
        }

        throw new InvalidInputException();
    }
}
