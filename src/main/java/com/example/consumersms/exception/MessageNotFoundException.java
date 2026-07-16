package com.example.consumersms.exception;

public class MessageNotFoundException extends MessageException {
    public MessageNotFoundException(Integer messageId) {
        super("Message not found: " + messageId, messageId);
    }
}