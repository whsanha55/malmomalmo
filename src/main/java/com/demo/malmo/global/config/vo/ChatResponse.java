package com.demo.malmo.global.config.vo;

import java.util.List;
import lombok.Data;

@Data
public class ChatResponse {

    private List<ChatChoice> choices;

    @Data
    public static class ChatChoice {

        private ChatMessage message;

    }
}
