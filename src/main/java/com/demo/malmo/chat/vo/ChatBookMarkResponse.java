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

        @Schema(description = "북마크된 ai 모자 대답 id", example = "123")
        Long chatAiRoomId;
        @Schema(description = "채팅방 id", example = "123")
        Long chatRoomId;
        @Schema(description = "채팅방 이름", example = "room name")
        String roomName;
        @Schema(description = "역할 모자", example = "BLUD_HAT")
        ChatRoleEnum role;
        @Schema(description = "대화 내용", example = "hello world")
        String message;

        public ChatBookMark(ChatAiMessageEntity entity) {
            this.chatAiRoomId = entity.getId();
            this.chatRoomId = entity.getChatRoomId();
            this.role = entity.getRole();
            this.message = entity.getMessage();
            this.roomName = entity.getChatRoom().getRoomName();
        }
    }

    public ChatBookMarkResponse(List<ChatAiMessageEntity> entities) {
        this.bookMarks = entities.stream()
            .map(ChatBookMark::new)
            .toList();
    }
}
