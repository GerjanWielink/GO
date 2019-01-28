package com.nedap.go.utilities.validators;

import com.nedap.go.utilities.exceptions.InvalidInputException;

public class BoardSizeValidator implements CommandInputValidator {

    @Override
    public Object validate(String message) throws InvalidInputException {
        try {
            int size = Integer.parseInt(message);
            if (size < 5 || size > 19) {
                throw new InvalidInputException();
            }

            return size;
        } catch (NumberFormatException e) {
            throw new InvalidInputException();
        }
    }
}
