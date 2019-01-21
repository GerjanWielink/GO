package com.nedap.go.client.validators;

import com.nedap.go.client.exceptions.InvalidInputException;

public class UsernameValidator implements CommandInputValidator {
    @Override
    public Object validate(String message) throws InvalidInputException {
        if (message.indexOf('+') != -1) {
            throw new InvalidInputException();
        }

        return message;
    }
}
