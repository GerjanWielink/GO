package com.nedap.go.client.validators;

import com.nedap.go.client.exceptions.InvalidInputException;

public interface CommandInputValidator {
    public Object validate(String message) throws InvalidInputException;
}
