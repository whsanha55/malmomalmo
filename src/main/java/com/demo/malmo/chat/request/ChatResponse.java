package com.demo.malmo.chat.request;

import com.demo.malmo.chat.enums.ChatRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ChatResponse {

    @Schema(description = "역할 모자", example = "BLUD_HAT")
    ChatRoleEnum role;

    @Schema(description = "대화 내용", example = "hello world")
    String message;
}
