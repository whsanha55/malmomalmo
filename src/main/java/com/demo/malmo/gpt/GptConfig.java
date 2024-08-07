package com.demo.malmo.gpt;

import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.classfile.Module.Open;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@ConfigurationProperties(prefix = "langchain")
@Configuration
@Data
@Slf4j
public class GptConfig {

    private String key;

    @Bean
    public OpenAiStreamingChatModel openAiStreamingChatModel() {

        /*     "topP": 0.8,
            "topK": 0,
            "maxTokens": 3743,
            "temperature": 0.11,
            "repeatPenalty": 1.2,*/
        return  OpenAiStreamingChatModel.builder()
            .modelName("gpt-4o-mini")
            .apiKey(key)
            .maxTokens(3743)
            .temperature(0.0)
            .topP(0.8)
            .build();
    }
}
