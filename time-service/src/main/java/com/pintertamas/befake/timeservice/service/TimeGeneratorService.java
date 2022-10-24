package com.pintertamas.befake.timeservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Random;

@Slf4j
@Component
public class TimeGeneratorService {

    private Timestamp beFakeTime = new Timestamp(System.currentTimeMillis());

    @Scheduled(cron = "0 0 0 * * *") // runs daily at 0:0:0
    //@Scheduled(cron = "*/1 * * * * *") // runs every second
    public void scheduleFixedDelayTask() {
        this.beFakeTime = generateTime();
    }

    TimeGeneratorService() {
        scheduleFixedDelayTask();
    }

    private Timestamp generateTime() {
        final Random random = new Random();
        final int hour = 60 * 60 * 1000;
        final int millisInDay = 24 * hour;
        Time time = new Time(random.nextInt(millisInDay));

        ZonedDateTime nowZoned = ZonedDateTime.now();
        Instant midnight = nowZoned.toLocalDate().atStartOfDay(nowZoned.getZone()).toInstant();
        Duration duration = Duration.between(midnight, Instant.now());
        long timeSinceMidnight = duration.getSeconds() * 1000;
        long now = nowZoned.toInstant().toEpochMilli();

        long beFakeTimeInMillis = now - timeSinceMidnight + time.getTime() + 3600000;

        log.info("Time since midnight: " + timeSinceMidnight);
        log.info("Time now: " + new Timestamp(now));
        log.info("Midnight: " + new Timestamp(now - timeSinceMidnight));
        log.info("Time in millis: " + time.getTime());
        log.info("Time: " + time);
        log.info("BeFake time: " + new Timestamp(beFakeTimeInMillis));

        return new Timestamp(beFakeTimeInMillis);
    }

    public Timestamp getBeFakeTime() {
        log.info(beFakeTime.toString());
        return beFakeTime;
    }
}
