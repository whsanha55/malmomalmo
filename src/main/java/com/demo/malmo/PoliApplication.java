package com.demo.malmo;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDynamoDBRepositories(basePackages = "com.demo.poli.*.repository")
@SpringBootApplication
public class PoliApplication {

    public static void main(String[] args) {
        SpringApplication.run(PoliApplication.class, args);

    }

}
