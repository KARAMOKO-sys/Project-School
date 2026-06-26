package com.edueasy.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan(
        basePackages = {"com.edueasy"}
)
@EntityScan(
        basePackages = {"com.edueasy.common.model"}
)
@EnableJpaRepositories(
        basePackages = {"com.edueasy.user.repository"}
)
public class UserServiceApplication {
    public UserServiceApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
