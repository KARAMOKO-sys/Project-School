package com.edueasy.registry;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication(
        exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class}
)
@EnableEurekaServer
public class EduEasyRegistryApplication {
    public EduEasyRegistryApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(EduEasyRegistryApplication.class, args);
    }
}
