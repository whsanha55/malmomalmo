package com.demo.malmo.chat.vo;

import com.demo.malmo.chat.entity.ChatUserMessageEntity;
import com.demo.malmo.global.base.BaseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class ChatMessageResponse extends BaseResponse {

    List<ChatUserMessage> rooms;
    int phase;

    public ChatMessageResponse(List<ChatUserMessageEntity> entities) {
        this.rooms = entities.stream()
            .map(ChatUserMessage::new)
            .toList();
        this.phase = entities.stream()
            .map(ChatUserMessageEntity::getPhase)
            .max(Comparator.naturalOrder())

            .orElse(1);
    }

    @Value
    @JsonInclude(Include.NON_EMPTY)
    public static class ChatUserMessage {

        @Schema(description = "채팅방 번호", example = "123")
        Long roomId;
        @Schema(description = "채팅 페이즈 아이디", example = "1")
        Long chatPhaseId;
        @Schema(description = "대화 턴 단계", example = "1")
        int phase;
        @Schema(description = "유저 메세지", example = "hello world")
        String userMessage;
        @Schema(description = "채팅방 카테고리", example = "room category")
        List<ChatAiMessageResponse> aiMessages;
        @Schema(description = "생성일", example = "2021-07-01T00:00:00.000000")
        LocalDateTime createdAt;

        public ChatUserMessage(ChatUserMessageEntity entity) {
            this.roomId = entity.getChatRoomId();
            this.chatPhaseId = entity.getId();
            this.userMessage = entity.getMessage();
            this.phase = entity.getPhase();
            this.aiMessages = Optional.ofNullable(entity.getChatAiMessages())
                .map(messages -> messages.stream()
                    .filter(message -> message.getRole().isHat())
                    .map(ChatAiMessageResponse::new)
                    .toList())
                .orElse(null);
            this.createdAt = entity.getCreatedAt();
        }
    }

}
