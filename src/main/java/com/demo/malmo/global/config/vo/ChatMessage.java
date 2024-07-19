package com.demo.malmo.global.config.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessage {

    private String role = "user";
    private String content;

    public ChatMessage(String content) {
        this.content = content;
    }
}
