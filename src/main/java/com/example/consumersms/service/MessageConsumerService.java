package com.example.consumersms.service;

import com.example.consumersms.dto.MessageDTO;
import com.example.consumersms.dto.MessageIdDTO;
import com.example.consumersms.entity.Message;
import com.example.consumersms.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(MessageConsumerService.class);

    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;

    public MessageConsumerService(MessageRepository messageRepository, ObjectMapper objectMapper) {
        this.messageRepository = messageRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "message-create-topic", groupId = "consumer-service-group")
    @Transactional
    public void handleCreate(String message) {
        try {
            MessageDTO dto = objectMapper.readValue(message, MessageDTO.class);

            if (messageRepository.existsById(dto.getId())) {
                logger.warn("✗ CREATE: Message {} already exists, skipping CREATE operation", dto.getId());
                return;
            }

            Message entity = new Message();
            entity.setId(dto.getId());
            entity.setMsg(dto.getMsg());
            entity.setOperation("CREATE");
            entity.setStatus("ACTIVE");

            messageRepository.save(entity);
            logger.info("✓ CREATE: Message {} saved successfully - Content: '{}'", dto.getId(), dto.getMsg());

        } catch (Exception e) {
            logger.error("✗ CREATE failed: Error processing message", e);
            throw new RuntimeException("Failed to process CREATE message", e);
        }
    }

    @KafkaListener(topics = "message-update-topic", groupId = "consumer-service-group")
    @Transactional
    public void handleUpdate(String message) {
        try {
            MessageDTO dto = objectMapper.readValue(message, MessageDTO.class);

            Message entity = messageRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Message not found: " + dto.getId()));

            String oldValue = entity.getMsg();
            entity.setMsg(dto.getMsg());
            entity.setOperation("UPDATE");

            messageRepository.save(entity);
            logger.info("✓ UPDATE: Message {} updated successfully - Old: '{}' -> New: '{}'",
                    dto.getId(), oldValue, dto.getMsg());

        } catch (RuntimeException e) {
            logger.error("✗ UPDATE failed: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("✗ UPDATE failed: Error processing message", e);
            throw new RuntimeException("Failed to process UPDATE message", e);
        }
    }

    @KafkaListener(topics = "message-delete-topic", groupId = "consumer-service-group")
    @Transactional
    public void handleDelete(String message) {
        try {
            MessageIdDTO dto = objectMapper.readValue(message, MessageIdDTO.class);

            Message entity = messageRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Message not found: " + dto.getId()));

            String previousStatus = entity.getStatus();
            entity.setStatus("DELETED");
            entity.setOperation("DELETE");

            messageRepository.save(entity);
            logger.info("✓ DELETE: Message {} marked as deleted - Status: {} -> DELETED",
                    dto.getId(), previousStatus);

        } catch (RuntimeException e) {
            logger.error("✗ DELETE failed: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("✗ DELETE failed: Error processing message", e);
            throw new RuntimeException("Failed to process DELETE message", e);
        }
    }

    @KafkaListener(topics = "message-read-topic", groupId = "consumer-service-group")
    public void handleRead(String message) {
        try {
            MessageIdDTO dto = objectMapper.readValue(message, MessageIdDTO.class);

            Message entity = messageRepository.findById(dto.getId()).orElse(null);

            if (entity != null && !"DELETED".equals(entity.getStatus())) {
                logger.info("✓ READ: Message {} retrieved successfully - Content: '{}' - Status: {}",
                        dto.getId(), entity.getMsg(), entity.getStatus());
            } else if (entity != null) {
                logger.warn("✗ READ: Message {} exists but is marked as DELETED", dto.getId());
            } else {
                logger.warn("✗ READ: Message {} not found in database", dto.getId());
            }

        } catch (Exception e) {
            logger.error("✗ READ failed: Error processing message", e);
        }
    }
}