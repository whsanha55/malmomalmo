package com.demo.malmo.chat.service;

import com.demo.malmo.chat.entity.ChatAiMessageEntity;
import com.demo.malmo.chat.entity.ChatRoomEntity;
import com.demo.malmo.chat.entity.ChatUserMessageEntity;
import com.demo.malmo.chat.enums.ChatRoleEnum;
import com.demo.malmo.chat.repository.ChatAiMessageRepository;
import com.demo.malmo.chat.repository.ChatRoomRepository;
import com.demo.malmo.chat.repository.ChatUserMessageRepository;
import com.demo.malmo.global.exception.BaseException;
import jakarta.transaction.Transactional;
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

    @Transactional
    public ChatRoomEntity createRoom(String userId, String chatCategory) {
        return chatRoomRepository.save(ChatRoomEntity.builder()
            .userId(userId)
            .category(chatCategory)
            .build());
    }

    @Transactional
    public ChatUserMessageEntity createChatUserMessage(ChatRoomEntity chatRoom, String message) {
        var phase = chatUserMessageRepository.countByChatRoomId(chatRoom.getId());
        return chatUserMessageRepository.save(ChatUserMessageEntity.builder()
            .chatRoomId(chatRoom.getId())
            .phase(phase + 1)
            .message(message)
            .build());
    }

    @Transactional
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

    public List<ChatRoomEntity> getChatRooms(String userId) {
        return chatRoomRepository.findByUserIdOrderByIdDesc(userId);
    }

    public List<ChatUserMessageEntity> getChatMessages(Long chatRoomId) {
        return chatUserMessageRepository.findByChatRoomId(chatRoomId);
    }

    public ChatAiMessageEntity getChatAiMessage(Long aiChatId) {
        return chatAiMessageRepository.findById(aiChatId)
            .orElseThrow(() -> new BaseException("ChatAiMessage not found"));
    }

    public List<ChatUserMessageEntity> getChatUserMessages(Long chatRoomId) {
        return chatUserMessageRepository.findByChatRoomId(chatRoomId);
    }

    public List<ChatAiMessageEntity> findBookMarkedChatAiMessages(String userId) {
        return chatAiMessageRepository.findBookmarked(userId);
    }

    @Transactional
    public ChatAiMessageEntity updateBookmark(Long aiChatId) {
        var aiChat = getChatAiMessage(aiChatId);
        aiChat.updateBookMark();
        return chatAiMessageRepository.save(aiChat);
    }

    @Transactional
    public void deleteRoom(Long chatRoomId) {
        var chatRoom = getRoom(chatRoomId);
        chatRoom.delete();
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void deleteAiChat(Long aiChatId) {
        var aiChat = getChatAiMessage(aiChatId);
        aiChat.delete();
        chatAiMessageRepository.save(aiChat);
    }


}
