package com.example.consumersms.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;

@Component
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public void handleMessageException(MessageException e) {
        if (e instanceof MessageNotFoundException) {
            logger.error("✗ OPERATION FAILED: Message not found [ID: {}]", e.getMessageId(), e);
        } else if (e instanceof InvalidMessageException) {
            logger.error("✗ OPERATION FAILED: Invalid message data [ID: {}] - {}",
                        e.getMessageId(), e.getMessage(), e);
        } else {
            logger.error("✗ OPERATION FAILED: Unexpected error [ID: {}]", e.getMessageId(), e);
        }
    }

    public void handleGenericException(Exception e) {
        logger.error("✗ OPERATION FAILED: Unexpected error", e);
    }
}