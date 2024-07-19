package com.demo.malmo.global.config.vo;

import java.util.List;
import lombok.Data;

@Data
public class ChatRequest {

    private String model;
    private List<ChatMessage> messages;
    private double temperature;
    private int max_tokens;
    private double top_p;
    private double frequency_penalty;
    private double presence_penalty;

}
