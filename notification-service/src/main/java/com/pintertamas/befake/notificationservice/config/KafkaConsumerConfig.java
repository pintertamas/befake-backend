package com.pintertamas.befake.notificationservice.config;

import com.pintertamas.befake.notificationservice.listener.KafkaBeFakeEventListener;
import com.pintertamas.befake.notificationservice.listener.KafkaInteractionEventListener;
import com.pintertamas.befake.notificationservice.listener.KafkaPostEventListener;
import com.pintertamas.befake.notificationservice.listener.KafkaUserEventListener;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${kafka.url}")
    private String bootstrapAddress;

    @Bean
    ConcurrentKafkaListenerContainerFactory<Integer, String>
    kafkaListenerContainerFactory(ConsumerFactory<Integer, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    public KafkaUserEventListener userEventListener() {
        return new KafkaUserEventListener();
    }

    @Bean
    public KafkaPostEventListener postEventListener() {
        return new KafkaPostEventListener();
    }

    @Bean
    public KafkaInteractionEventListener interactionEventListener() {
        return new KafkaInteractionEventListener();
    }

    @Bean
    public KafkaBeFakeEventListener beFakeEventListener() {
        return new KafkaBeFakeEventListener();
    }
}