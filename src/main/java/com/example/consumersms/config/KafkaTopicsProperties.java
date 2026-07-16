package com.example.consumersms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaTopicsProperties {

    private String consumerGroup;
    private Topics topics;

    public KafkaTopicsProperties() {
        this.topics = new Topics();
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public Topics getTopics() {
        return topics;
    }

    public void setTopics(Topics topics) {
        this.topics = topics;
    }

    public static class Topics {
        private String messageCreate;
        private String messageUpdate;
        private String messageDelete;
        private String messageRead;

        public String getMessageCreate() {
            return messageCreate;
        }

        public void setMessageCreate(String messageCreate) {
            this.messageCreate = messageCreate;
        }

        public String getMessageUpdate() {
            return messageUpdate;
        }

        public void setMessageUpdate(String messageUpdate) {
            this.messageUpdate = messageUpdate;
        }

        public String getMessageDelete() {
            return messageDelete;
        }

        public void setMessageDelete(String messageDelete) {
            this.messageDelete = messageDelete;
        }

        public String getMessageRead() {
            return messageRead;
        }

        public void setMessageRead(String messageRead) {
            this.messageRead = messageRead;
        }
    }
}