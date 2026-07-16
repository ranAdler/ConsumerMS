package com.example.consumersms.service;

import com.example.consumersms.dto.MessageDTO;
import com.example.consumersms.dto.MessageIdDTO;
import com.example.consumersms.entity.Message;
import com.example.consumersms.exception.MessageNotFoundException;
import com.example.consumersms.mapper.MessageMapper;
import com.example.consumersms.repository.MessageRepository;
import com.example.consumersms.validator.MessageValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(MessageProcessingService.class);

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final MessageValidator messageValidator;

    public MessageProcessingService(MessageRepository messageRepository,
                                     MessageMapper messageMapper,
                                     MessageValidator messageValidator) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.messageValidator = messageValidator;
    }

    @Transactional
    public void create(MessageDTO dto) {
        messageValidator.validateMessageDTO(dto);

        if (messageRepository.existsById(dto.getId())) {
            logger.warn("✗ CREATE: Message {} already exists, skipping CREATE operation", dto.getId());
            return;
        }

        Message entity = messageMapper.toEntity(dto);
        entity.setOperation("CREATE");
        entity.setStatus("ACTIVE");

        messageRepository.save(entity);
        logger.info("✓ CREATE: Message {} saved successfully - Content: '{}'",
                    dto.getId(), dto.getMsg());
    }

    @Transactional
    public void update(MessageDTO dto) {
        messageValidator.validateMessageDTO(dto);

        Message entity = messageRepository.findById(dto.getId())
                .orElseThrow(() -> new MessageNotFoundException(dto.getId()));

        String oldValue = entity.getMsg();
        entity.setMsg(dto.getMsg());
        entity.setOperation("UPDATE");

        messageRepository.save(entity);
        logger.info("✓ UPDATE: Message {} updated successfully - Old: '{}' -> New: '{}'",
                    dto.getId(), oldValue, dto.getMsg());
    }

    @Transactional
    public void delete(MessageIdDTO dto) {
        messageValidator.validateMessageIdDTO(dto);

        Message entity = messageRepository.findById(dto.getId())
                .orElseThrow(() -> new MessageNotFoundException(dto.getId()));

        String previousStatus = entity.getStatus();
        entity.setStatus("DELETED");
        entity.setOperation("DELETE");

        messageRepository.save(entity);
        logger.info("✓ DELETE: Message {} marked as deleted - Status: {} -> DELETED",
                    dto.getId(), previousStatus);
    }

    public void read(MessageIdDTO dto) {
        messageValidator.validateMessageIdDTO(dto);

        Message entity = messageRepository.findById(dto.getId()).orElse(null);

        if (entity != null && !"DELETED".equals(entity.getStatus())) {
            logger.info("✓ READ: Message {} retrieved successfully - Content: '{}' - Status: {}",
                        dto.getId(), entity.getMsg(), entity.getStatus());
        } else if (entity != null) {
            logger.warn("✗ READ: Message {} exists but is marked as DELETED", dto.getId());
        } else {
            logger.warn("✗ READ: Message {} not found in database", dto.getId());
        }
    }
}