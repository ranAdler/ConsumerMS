package com.example.consumersms.exception;

public abstract class MessageException extends RuntimeException {
    private final Integer messageId;

    public MessageException(String message, Integer messageId) {
        super(message);
        this.messageId = messageId;
    }

    public MessageException(String message, Integer messageId, Throwable cause) {
        super(message, cause);
        this.messageId = messageId;
    }

    public Integer getMessageId() {
        return messageId;
    }
}