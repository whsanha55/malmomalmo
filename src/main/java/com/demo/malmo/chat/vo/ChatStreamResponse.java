package com.demo.malmo.chat.vo;

import com.demo.malmo.chat.entity.ChatAiMessageEntity;
import com.demo.malmo.chat.enums.ChatRoleEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class ChatStreamResponse {

    @Schema(description = "대화방 ID", example = "123")
    Long roomId;

    @Schema(description = "대화방 페이즈 id", example = "1")
    Long roomPhaseId;
    @Schema(description = "aiMessageId", example = "123")
    Long aiMessageId;

    @Schema(description = "역할 모자", example = "BLUD_HAT")
    ChatRoleEnum role;

    @Schema(description = "대화 내용", example = "hello world")
    String message;

    public ChatStreamResponse(ChatAiMessageEntity entity, String message) {
        this.aiMessageId = entity.getId();
        this.roomId = entity.getChatRoomId();
        this.roomPhaseId = entity.getChatUserMessageId();
        this.role = entity.getRole();
        this.message = message;
    }

}
