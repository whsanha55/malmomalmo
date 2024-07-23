package com.demo.malmo.global.api.clova.vo;

import com.demo.malmo.chat.entity.ChatUserMessageEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Builder
@AllArgsConstructor
@Data
@Slf4j
@JsonInclude(Include.NON_DEFAULT)
public class HyperClovaRequest {

    private List<Message> messages;
    @Builder.Default
    private double topP = 0.8;
    @Builder.Default
    private int topK = 0;
    @Builder.Default
    private int maxTokens = 4096;
    @Builder.Default
    private double temperature = 0.1;
    @Builder.Default
    private double repeatPenalty = 1.2;
    private List<String> stopBefore;
    private boolean includeAiFilters;
    private int seed;

    @Builder
    @AllArgsConstructor
    @Data
    public static class Message {

        private String role;
        private String content;
    }

    public HyperClovaRequest(List<ChatUserMessageEntity> chatUserMessages, String chatMessage) {
        this.messages = Stream.concat(chatUserMessages.stream()
                    .map(message -> List.of(Message.builder()
                            .role("user")
                            .content(message.getMessage())
                            .build(),
                        Message.builder()
                            .role("assistant")
                            .content(message.getChatAiMessages().get(0).getMessage())
                            .build())
                    )
                    .flatMap(List::stream),
                Stream.of(Message.builder()
                    .role("user")
                    .content(chatMessage)
                    .build()))
            .toList();

    }

}
