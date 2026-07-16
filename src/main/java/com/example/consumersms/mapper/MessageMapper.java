package com.example.consumersms.mapper;

import com.example.consumersms.dto.MessageDTO;
import com.example.consumersms.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public Message toEntity(MessageDTO dto) {
        Message message = new Message();
        message.setId(dto.getId());
        message.setMsg(dto.getMsg());
        return message;
    }

    public MessageDTO toDTO(Message entity) {
        return new MessageDTO(entity.getId(), entity.getMsg());
    }
}