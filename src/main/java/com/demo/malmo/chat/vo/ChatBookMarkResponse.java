package com.demo.malmo.chat.vo;

import com.demo.malmo.chat.entity.ChatAiMessageEntity;
import com.demo.malmo.chat.enums.ChatRoleEnum;
import com.demo.malmo.global.base.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class ChatBookMarkResponse extends BaseResponse {

    List<ChatBookMark> bookMarks;

    @Value
    public static class ChatBookMark {

        @Schema(description = "채팅방 id", example = "123")
        Long roomId;
        @Schema(description = "대화방 페이즈 id", example = "1")
        Long roomPhaseId;
        @Schema(description = "북마크된 ai 모자 대답 id", example = "123")
        Long aiMessageId;
        @Schema(description = "채팅방 이름", example = "room name")
        String roomName;
        @Schema(description = "대화 턴 단계", example = "1")
        int phase;
        @Schema(description = "역할 모자", example = "BLUD_HAT")
        ChatRoleEnum role;
        @Schema(description = "대화 내용", example = "hello world")
        String message;

        public ChatBookMark(ChatAiMessageEntity entity) {
            this.roomId = entity.getChatRoomId();
            this.roomPhaseId = entity.getChatUserMessage().getId();
            this.aiMessageId = entity.getId();
            this.role = entity.getRole();
            this.message = entity.getMessage();
            this.phase = entity.getChatUserMessage().getPhase();
            this.roomName = entity.getChatRoom().getRoomName();
        }
    }

    public ChatBookMarkResponse(List<ChatAiMessageEntity> entities) {
        this.bookMarks = entities.stream()
            .map(ChatBookMark::new)
            .toList();
    }
}
