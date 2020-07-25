package com.learn.demobatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Tutorial from: https://www.youtube.com/watch?v=1XEX-u12i0A
 */
@SpringBootApplication
@EnableBatchProcessing
public class DemoBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoBatchApplication.class, args);
    }

}
