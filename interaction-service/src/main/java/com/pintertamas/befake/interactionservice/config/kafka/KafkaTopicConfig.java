package com.pintertamas.befake.interactionservice.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    private static final String BOOTSTRAP_ADDRESS = "localhost:9092";
    private static final String TOPIC_COMMENT = "comment";
    private static final String TOPIC_REACTION = "reaction";

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_ADDRESS);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic commentTopic() {
        return new NewTopic(TOPIC_COMMENT, 2, (short) 3);
    }

    @Bean
    public NewTopic reactionTopic() {
        return new NewTopic(TOPIC_REACTION, 2, (short) 3);
    }
}