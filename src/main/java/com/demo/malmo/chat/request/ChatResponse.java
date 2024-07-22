package com.demo.malmo.chat.request;

import com.demo.malmo.chat.entity.ChatAiMessageEntity;
import com.demo.malmo.chat.enums.ChatRoleEnum;
import com.demo.malmo.global.base.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
@Builder
@AllArgsConstructor
public class ChatResponse extends BaseResponse {

    @Schema(description = "aiChatId", example = "123")
    Long id;

    @Schema(description = "역할 모자", example = "BLUD_HAT")
    ChatRoleEnum role;

    @Schema(description = "대화 내용", example = "hello world")
    String message;

    public ChatResponse(ChatAiMessageEntity entity) {
        this.id = entity.getId();
        this.role = entity.getRole();
        this.message = entity.getMessage();
    }
}
