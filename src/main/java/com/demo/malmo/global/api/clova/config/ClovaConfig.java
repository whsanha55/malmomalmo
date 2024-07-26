package com.demo.malmo.global.api.clova.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@ConfigurationProperties(prefix = "api.clova")
@Configuration
@Data
@Slf4j
public class ClovaConfig {

    private String studioApiKey;
    private String gatewayKey;
    private String requestId;


    @PostConstruct
    public void init() {
        log.info("this : {}", this);
    }

    @Bean
    public WebClient clovaWebClient() {
        return WebClient.builder()
            .baseUrl("http://223.130.157.144:8000/")
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
}
