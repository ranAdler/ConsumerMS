package com.example.consumersms.exception;

public class InvalidMessageException extends MessageException {
    public InvalidMessageException(String message, Integer messageId, Throwable cause) {
        super("Invalid message data: " + message, messageId, cause);
    }
}