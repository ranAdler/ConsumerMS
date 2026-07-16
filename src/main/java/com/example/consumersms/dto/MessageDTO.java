package com.example.consumersms.dto;

public class MessageDTO {
    private Integer id;
    private String msg;

    public MessageDTO() {
    }

    public MessageDTO(Integer id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "id=" + id +
                ", msg='" + msg + '\'' +
                '}';
    }
}