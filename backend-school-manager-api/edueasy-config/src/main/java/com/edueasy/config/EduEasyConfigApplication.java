package com.edueasy.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class EduEasyConfigApplication {
    public EduEasyConfigApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(EduEasyConfigApplication.class, args);
    }
}