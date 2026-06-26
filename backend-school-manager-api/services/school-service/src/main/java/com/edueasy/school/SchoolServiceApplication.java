package com.edueasy.school;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EntityScan(
        basePackages = {"com.edueasy.school.model"}
)
@EnableJpaRepositories(
        basePackages = {"com.edueasy.school.repository"}
)
public class SchoolServiceApplication {
    public SchoolServiceApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(SchoolServiceApplication.class, args);
    }
}
