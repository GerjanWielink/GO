package com.nedap.go.utilities;

import com.nedap.go.utilities.exceptions.InvalidInputException;
import com.nedap.go.utilities.validators.CommandInputValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class IO {
    static public Object promptInput(String message, CommandInputValidator validator) {
        System.out.print(message);
        String response = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            response = in.readLine();

            return validator.validate(response);

        } catch (IOException | InvalidInputException e) {
            return promptInput(message, validator);
        }
    }
}
