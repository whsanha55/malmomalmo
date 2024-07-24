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
            .baseUrl("https://clovastudio.stream.ntruss.com")
            .defaultHeader("X-NCP-CLOVASTUDIO-API-KEY", studioApiKey)
            .defaultHeader("X-NCP-APIGW-API-KEY", gatewayKey)
            .defaultHeader("Content-Type", "application/json")
            .defaultHeader("Accept", "text/event-stream")
            .build();
    }
}
