package com.demo.malmo.chat.service;

import com.demo.malmo.chat.entity.ChatAiMessageEntity;
import com.demo.malmo.chat.entity.ChatRoomEntity;
import com.demo.malmo.chat.entity.ChatUserMessageEntity;
import com.demo.malmo.chat.enums.ChatRoleEnum;
import com.demo.malmo.chat.repository.ChatAiMessageRepository;
import com.demo.malmo.chat.repository.ChatRoomRepository;
import com.demo.malmo.chat.repository.ChatUserMessageRepository;
import com.demo.malmo.global.exception.BaseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatService {


    private final ChatRoomRepository chatRoomRepository;
    private final ChatUserMessageRepository chatUserMessageRepository;
    private final ChatAiMessageRepository chatAiMessageRepository;

    public ChatRoomEntity createRoom(String userId, String chatCategory) {
        return chatRoomRepository.save(ChatRoomEntity.builder()
            .userId(userId)
            .category(chatCategory)
            .build());
    }

    public ChatUserMessageEntity createChatUserMessage(ChatRoomEntity chatRoom, String message) {
        return chatUserMessageRepository.save(ChatUserMessageEntity.builder()
            .chatRoomId(chatRoom.getId())
            .message(message)
            .build());
    }

    public ChatAiMessageEntity createChatAiMessage(ChatUserMessageEntity chatUserMessage, String message, ChatRoleEnum role) {
        return chatAiMessageRepository.save(ChatAiMessageEntity.builder()
            .chatRoomId(chatUserMessage.getChatRoomId())
            .chatUserMessageId(chatUserMessage.getId())
            .message(message)
            .role(role)
            .build());
    }

    public ChatRoomEntity getRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new BaseException("ChatRoom not found"));
    }

    public List<ChatUserMessageEntity> getChatUserMessages(Long chatRoomId) {
        return chatUserMessageRepository.findByChatRoomId(chatRoomId);
    }


}
