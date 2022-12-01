package com.pintertamas.befake.postservice.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Timestamp;

@FeignClient(name = "time-service")
public interface TimeServiceProxy {
    @GetMapping("/time/last-befake-time")
    ResponseEntity<Timestamp> getLastBeFakeTime();
}
