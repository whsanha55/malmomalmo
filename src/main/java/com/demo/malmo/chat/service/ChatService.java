package com.demo.malmo.chat.service;

import com.demo.malmo.chat.entity.ChatAiMessageEntity;
import com.demo.malmo.chat.entity.ChatRoomEntity;
import com.demo.malmo.chat.entity.ChatUserMessageEntity;
import com.demo.malmo.chat.enums.ChatRoleEnum;
import com.demo.malmo.chat.repository.ChatAiMessageRepository;
import com.demo.malmo.chat.repository.ChatRoomRepository;
import com.demo.malmo.chat.repository.ChatUserMessageRepository;
import com.demo.malmo.global.exception.BaseException;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatService {


    private final ChatRoomRepository chatRoomRepository;
    private final ChatUserMessageRepository chatUserMessageRepository;
    private final ChatAiMessageRepository chatAiMessageRepository;

    @Transactional
    public ChatRoomEntity createRoom(String userId, String chatCategory, String message) {
        return chatRoomRepository.save(ChatRoomEntity.builder()
            .userId(userId)
            .category(chatCategory)
                .roomName(StringUtils.substring(message, 0, 100))
            .build());
    }

    @Transactional
    public ChatUserMessageEntity createChatUserMessage(ChatRoomEntity chatRoom, String message, @Nullable Long relyAiMessageId) {
        var phase = chatUserMessageRepository.countByChatRoomId(chatRoom.getId());
        return chatUserMessageRepository.save(ChatUserMessageEntity.builder()
            .chatRoomId(chatRoom.getId())
            .relyAiMessageId(relyAiMessageId)
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

    @Transactional
    public ChatAiMessageEntity createChatAiMessage(ChatAiMessageEntity entity) {
        return chatAiMessageRepository.save(entity);
    }

    public ChatRoomEntity getRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new BaseException("ChatRoom not found"));
    }

    public List<ChatRoomEntity> getChatRooms(String userId) {
        return chatRoomRepository.findByUserIdOrderByIdDesc(userId);
    }

    public ChatUserMessageEntity getChatUserMessage(Long chatPhaseId) {

        return chatUserMessageRepository.findById(chatPhaseId).orElseThrow(() -> new BaseException("ChatUserMessage not found"));
    }

    public List<ChatUserMessageEntity> getChatMessages(Long chatRoomId) {
        return chatUserMessageRepository.findByChatRoomId(chatRoomId);
    }

    public ChatAiMessageEntity getChatAiMessage(Long aiMessageId) {
        return chatAiMessageRepository.findById(aiMessageId)
            .orElseThrow(() -> new BaseException("ChatAiMessage not found"));
    }

    public List<ChatUserMessageEntity> getChatUserMessages(Long chatRoomId) {
        return chatUserMessageRepository.findByChatRoomId(chatRoomId);
    }

    public List<ChatAiMessageEntity> findBookMarkedChatAiMessages(String userId) {
        return chatAiMessageRepository.findBookmarked(userId);
    }

    public List<ChatAiMessageEntity> getChatAiMessages(Long chatRoomId, ChatRoleEnum role) {
        return chatAiMessageRepository.findByChatRoomIdAndRole(chatRoomId, role);
    }

    @Transactional
    public ChatAiMessageEntity updateBookmark(Long aiMessageId) {
        var aiMessage = getChatAiMessage(aiMessageId);
        aiMessage.updateBookMark();
        return chatAiMessageRepository.save(aiMessage);
    }

    @Transactional
    public void deleteRoom(Long chatRoomId) {
        var chatRoom = getRoom(chatRoomId);
        chatRoom.delete();
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void deleteAiChat(Long aiMesssageId) {
        var aiMessage = getChatAiMessage(aiMesssageId);
        aiMessage.delete();
        chatAiMessageRepository.save(aiMessage);
    }


    @Transactional
    public void updateMessage(ChatAiMessageEntity entity, String message) {
        entity.updateMessage(message);
        chatAiMessageRepository.save(entity);
    }

    @Transactional
    public void updateRoomName(Long roomId, String roomName) {
        var room = getRoom(roomId);
        room.updateName(roomName);
        chatRoomRepository.save(room);
    }
}
