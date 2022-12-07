package com.pintertamas.befake.loadtestvalidationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class LoadTestValidationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoadTestValidationServiceApplication.class, args);
    }

}
