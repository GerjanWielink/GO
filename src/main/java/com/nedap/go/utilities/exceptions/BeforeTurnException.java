package com.nedap.go.utilities.exceptions;

public class BeforeTurnException extends InvalidMoveException {
    public BeforeTurnException() {
        super();
    }

    public BeforeTurnException(String message) {
        super(message);
    }
}
