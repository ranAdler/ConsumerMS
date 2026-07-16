package com.example.consumersms.service;

import com.example.consumersms.dto.MessageDTO;
import com.example.consumersms.dto.MessageIdDTO;
import com.example.consumersms.exception.GlobalExceptionHandler;
import com.example.consumersms.exception.MessageException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumerService {

    private final JsonDeserializerService deserializerService;
    private final MessageProcessingService processingService;
    private final GlobalExceptionHandler exceptionHandler;

    public MessageConsumerService(JsonDeserializerService deserializerService,
                                  MessageProcessingService processingService,
                                  GlobalExceptionHandler exceptionHandler) {
        this.deserializerService = deserializerService;
        this.processingService = processingService;
        this.exceptionHandler = exceptionHandler;
    }

    @KafkaListener(topics = "message-create-topic", groupId = "consumer-service-group")
    public void handleCreate(String message) {
        try {
            MessageDTO dto = deserializerService.deserializeMessageDTO(message);
            processingService.create(dto);
        } catch (MessageException e) {
            exceptionHandler.handleMessageException(e);
        } catch (Exception e) {
            exceptionHandler.handleGenericException(e);
        }
    }

    @KafkaListener(topics = "message-update-topic", groupId = "consumer-service-group")
    public void handleUpdate(String message) {
        try {
            MessageDTO dto = deserializerService.deserializeMessageDTO(message);
            processingService.update(dto);
        } catch (MessageException e) {
            exceptionHandler.handleMessageException(e);
        } catch (Exception e) {
            exceptionHandler.handleGenericException(e);
        }
    }

    @KafkaListener(topics = "message-delete-topic", groupId = "consumer-service-group")
    public void handleDelete(String message) {
        try {
            MessageIdDTO dto = deserializerService.deserializeMessageIdDTO(message);
            processingService.delete(dto);
        } catch (MessageException e) {
            exceptionHandler.handleMessageException(e);
        } catch (Exception e) {
            exceptionHandler.handleGenericException(e);
        }
    }

    @KafkaListener(topics = "message-read-topic", groupId = "consumer-service-group")
    public void handleRead(String message) {
        try {
            MessageIdDTO dto = deserializerService.deserializeMessageIdDTO(message);
            processingService.read(dto);
        } catch (MessageException e) {
            exceptionHandler.handleMessageException(e);
        } catch (Exception e) {
            exceptionHandler.handleGenericException(e);
        }
    }
}