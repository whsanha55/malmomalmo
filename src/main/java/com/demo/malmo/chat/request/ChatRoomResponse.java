package com.demo.malmo.chat.request;

import com.demo.malmo.chat.entity.ChatRoomEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Value;

@Value
public class ChatRoomResponse {

    List<ChatRoom> rooms;

    public ChatRoomResponse(List<ChatRoomEntity> entities) {
        this.rooms = entities.stream()
            .map(ChatRoom::new)
            .toList();
    }

    @Value
    public static class ChatRoom {

        @Schema(description = "채팅방 고유 번호", example = "123")
        Long id;
        @Schema(description = "채팅방 제목", example = "room name")
        String roomName;

        @Schema(description = "채팅방 카테고리", example = "room category")
        String category;


        public ChatRoom(ChatRoomEntity entity) {
            this.id = entity.getId();
            this.roomName = entity.getRoomName();
            this.category = entity.getCategory();
        }
    }

}
