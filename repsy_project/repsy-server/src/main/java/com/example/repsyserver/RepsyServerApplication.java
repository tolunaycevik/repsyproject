package com.example.repsyserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example")
public class RepsyServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RepsyServerApplication.class, args);
    }
}
