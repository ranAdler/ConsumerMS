package com.example.consumersms.service;

import com.example.consumersms.dto.MessageDTO;
import com.example.consumersms.dto.MessageIdDTO;
import com.example.consumersms.exception.InvalidMessageException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class JsonDeserializerService {

    private final ObjectMapper objectMapper;

    public JsonDeserializerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public MessageDTO deserializeMessageDTO(String json) {
        try {
            return objectMapper.readValue(json, MessageDTO.class);
        } catch (Exception e) {
            throw new InvalidMessageException("Failed to parse MessageDTO from JSON", null, e);
        }
    }

    public MessageIdDTO deserializeMessageIdDTO(String json) {
        try {
            return objectMapper.readValue(json, MessageIdDTO.class);
        } catch (Exception e) {
            throw new InvalidMessageException("Failed to parse MessageIdDTO from JSON", null, e);
        }
    }
}