package com.pintertamas.befake.postservice.config.kafka;

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
    private static final String TOPIC = "post";

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_ADDRESS);
        return new KafkaAdmin(configs);
    }
    
    @Bean
    public NewTopic postTopic() {
         return new NewTopic(TOPIC, 2, (short) 3);
    }
}