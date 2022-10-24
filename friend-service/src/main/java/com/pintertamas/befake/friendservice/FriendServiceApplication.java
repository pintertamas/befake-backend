package com.pintertamas.befake.friendservice;

import com.pintertamas.befake.friendservice.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class FriendServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FriendServiceApplication.class, args);
    }

}
