package com.pintertamas.befake.loadtestvalidationservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
public class LoadTestTextController {

    @GetMapping("/{filename}")
    public ResponseEntity<?> download(@PathVariable String filename) {

        log.info("Getting loader.io validation file");
        log.info("filename is: " + filename);

        try {
            String fileContent = filename.substring(0, filename.length() - 4); // removes the .txt substring
            log.info("filename without .txt is: " + fileContent);

            //File file = new File("filename");
            //FileUtils.writeStringToFile(file, fileContent, StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .contentLength(fileContent.length())
                    //.contentType(MediaType.TEXT_PLAIN)
                    .body(fileContent);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }
}
