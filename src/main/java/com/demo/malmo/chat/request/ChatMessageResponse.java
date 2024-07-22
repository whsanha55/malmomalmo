package com.demo.malmo.chat.request;

import com.demo.malmo.chat.entity.ChatUserMessageEntity;
import com.demo.malmo.global.base.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class ChatMessageResponse extends BaseResponse {

    List<ChatUserMessage> rooms;

    public ChatMessageResponse(List<ChatUserMessageEntity> entities) {
        this.rooms = entities.stream()
            .map(ChatUserMessage::new)
            .toList();
    }

    @Value
    public static class ChatUserMessage {

        @Schema(description = "유저 메세지", example = "hello world")
        String userMessage;

        @Schema(description = "채팅방 카테고리", example = "room category")
        List<ChatResponse> aiMessages;

        @Schema(description = "생성일", example = "2021-07-01T00:00:00.000000")
        LocalDateTime createdAt;

        public ChatUserMessage(ChatUserMessageEntity entity) {
            this.userMessage = entity.getMessage();
            this.aiMessages = entity.getChatAiMessages().stream()
                .map(ChatResponse::new)
                .toList();
            this.createdAt = entity.getCreatedAt();
        }
    }

}
