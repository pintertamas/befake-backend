package com.pintertamas.befake.timegenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TimeGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimeGeneratorApplication.class, args);
    }

}
