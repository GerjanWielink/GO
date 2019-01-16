package com.nedap.go.utilities.exceptions;

public class NotEmptyException extends InvalidMoveException {
    public NotEmptyException (String message) {
        super(message);
    }
}
