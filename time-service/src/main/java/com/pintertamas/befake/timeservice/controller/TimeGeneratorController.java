package com.pintertamas.befake.timeservice.controller;

import com.pintertamas.befake.timeservice.service.KafkaService;
import com.pintertamas.befake.timeservice.service.TimeGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@Slf4j
@RestController
@RequestMapping("/time")
public class TimeGeneratorController {

    private final TimeGeneratorService timeGeneratorService;
    private final KafkaService kafkaService;

    public TimeGeneratorController(TimeGeneratorService timeGeneratorService, KafkaService kafkaService) {
        this.timeGeneratorService = timeGeneratorService;
        this.kafkaService = kafkaService;
    }

    @GetMapping("/last-befake-time")
    public ResponseEntity<Timestamp> getLastBeFakeTime() {
        try {
            Timestamp lastBeFakeTime = timeGeneratorService.getBeFakeTime();
            return new ResponseEntity<>(lastBeFakeTime, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/kafka-test")
    public ResponseEntity<String> kafkaTest() {
        kafkaService.sendBeFakeTimeNotification();
        return ResponseEntity.ok().build();
    }

}
