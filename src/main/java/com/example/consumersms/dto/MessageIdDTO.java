package com.example.consumersms.dto;

public class MessageIdDTO {
    private Integer id;

    public MessageIdDTO() {
    }

    public MessageIdDTO(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MessageIdDTO{" +
                "id=" + id +
                '}';
    }
}