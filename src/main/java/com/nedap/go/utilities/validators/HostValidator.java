package com.nedap.go.utilities.validators;

import com.nedap.go.utilities.exceptions.InvalidInputException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostValidator implements CommandInputValidator {

    @Override
    public Object validate(String message) throws InvalidInputException {
        try {
            if (message.equals("")) {
                return InetAddress.getByName("Localhost");
            }

            InetAddress host = InetAddress.getByName(message);
            return host;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new InvalidInputException();
        }
    }
}