package com.demo.malmo.chat.vo;

import com.demo.malmo.chat.enums.ChatRoleEnum;
import com.demo.malmo.chat.enums.GptTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChatStreamRequest {

    @Schema(description = "채팅방 페이즈 id", example = "1")
    private Long chatPhaseId;

    @Schema(description = "역할 모자", example = "BLUE_HAT")
    ChatRoleEnum role;

    @Schema(description = "GPT 타입 HYPER_CLOVA, OPEN_AI...", example = "HYPER_CLOVA", defaultValue = "HYPER_CLOVA")
    GptTypeEnum gptType = GptTypeEnum.HYPER_CLOVA;
}
