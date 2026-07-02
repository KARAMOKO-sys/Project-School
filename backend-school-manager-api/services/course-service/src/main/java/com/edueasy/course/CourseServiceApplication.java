package com.edueasy.course;

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
@ComponentScan(basePackages = {
        "com.edueasy.course",
        "com.edueasy.common"  // 🔥 AJOUTER CE PACKAGE POUR SCANNER JwtUtil
})
@EntityScan(basePackages = {
        "com.edueasy.course.model",
        "com.edueasy.common.model"  // 🔥 AJOUTER POUR LES ENTITÉS COMMUNES
})
@EnableJpaRepositories(basePackages = {
        "com.edueasy.course.repository"
})
public class CourseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseServiceApplication.class, args);
    }
}