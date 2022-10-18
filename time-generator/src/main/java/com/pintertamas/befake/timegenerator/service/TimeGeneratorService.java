package com.pintertamas.befake.timegenerator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.Random;

@Slf4j
@Component
public class TimeGeneratorService {

    private Time beFakeTime = new Time(0);

    @Scheduled(cron = "0 0 0 * * *") // runs daily at 0:0:0
    //@Scheduled(cron = "*/1 * * * * *") // runs every second
    public void scheduleFixedDelayTask() {
        this.beFakeTime = generateTime();
    }

    private Time generateTime() {
        final Random random = new Random();
        final int millisInDay = 24*60*60*1000;
        Time time = new Time(random.nextInt(millisInDay));
        log.info("BeFake today: " + time);
        return time;
    }

    @Scheduled(cron = "* * */1 * * *") // runs every one hour
    public void assureGeneratedTime() {
        //todo: make sure the time will be generated every day, even if the service stops working for a while
    }
}
