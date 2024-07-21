package com.demo.malmo.global.api.clova.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClovaResponse {

    private Message message;
    private int index;
    private int inputLength;
    private int outputLength;
    private String stopReason;
    private long seed;
    private Object data;

    public boolean isStream() {
        if (data != null) {
            return false;
        }
        return seed <= 0;
    }

    public boolean isResult() {
        if (data != null) {
            return false;
        }
        return seed > 0;
    }

    public String getMessage() {
        if (message == null) {
            return null;

        }
        return message.getContent();
    }


    @Data
    @NoArgsConstructor
    public static class Message {

        private String role;
        private String content;

    }

}
