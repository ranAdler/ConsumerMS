package com.example.consumersms.validator;

import com.example.consumersms.dto.MessageDTO;
import com.example.consumersms.dto.MessageIdDTO;
import com.example.consumersms.exception.InvalidMessageException;
import org.springframework.stereotype.Component;

@Component
public class MessageValidator {

    public void validateMessageDTO(MessageDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new InvalidMessageException("Message ID cannot be null", null, null);
        }
        if (dto.getMsg() == null || dto.getMsg().trim().isEmpty()) {
            throw new InvalidMessageException("Message content cannot be empty", dto.getId(), null);
        }
    }

    public void validateMessageIdDTO(MessageIdDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new InvalidMessageException("Message ID cannot be null", null, null);
        }
    }
}