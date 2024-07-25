package com.demo.malmo.chat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChatRequest {

    @Schema(description = "대화 카테고리(최초인 경우 입력)", example = "사업/서비스", nullable = true)
    private String category;

    @Schema(description = "채팅방 번호(기존 채팅방에서 지속적으로 대화하는 경우 입력)", example = "123", nullable = true)
    private Long roomId;
    @Schema(description = "대화 내용", example = "hello world")
    private String message;

    @Schema(description = "모자 선택 후 추가 페이즈 진행 시 ai 답변 id(nullable)", example = "", nullable = true)
    private Long relyAiMessageId;
}
