package com.nedap.go.utilities.validators;

import com.nedap.go.utilities.exceptions.InvalidInputException;

public interface CommandInputValidator {
    public Object validate(String message) throws InvalidInputException;
}
