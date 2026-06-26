package com.edueasy.timetable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
@EntityScan(
        basePackages = {"com.edueasy.timetable.model"}
)
@EnableJpaRepositories(
        basePackages = {"com.edueasy.timetable.repository"}
)
public class TimetableServiceApplication {
    public TimetableServiceApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(TimetableServiceApplication.class, args);
    }
}
