package com.example.consumersms.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_operation", columnList = "operation"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_last_updated", columnList = "last_updated"),
    @Index(name = "idx_status_operation", columnList = "status, operation")
})
public class Message {

    @Id
    private Integer id;

    @Column(name = "msg", length = 500)
    private String msg;

    @Column(name = "operation", length = 50)
    private String operation;

    @Column(name = "status", length = 50)
    private String status;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Message() {
    }

    public Message(Integer id, String msg, String operation, String status) {
        this.id = id;
        this.msg = msg;
        this.operation = operation;
        this.status = status;
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

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", msg='" + msg + '\'' +
                ", operation='" + operation + '\'' +
                ", status='" + status + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", createdAt=" + createdAt +
                '}';
    }
}