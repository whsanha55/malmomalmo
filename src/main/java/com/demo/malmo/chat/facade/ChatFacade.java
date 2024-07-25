package com.demo.malmo.chat.facade;

import com.demo.malmo.chat.entity.ChatAiMessageEntity;
import com.demo.malmo.chat.entity.ChatUserMessageEntity;
import com.demo.malmo.chat.enums.ChatRoleEnum;
import com.demo.malmo.chat.enums.GptTypeEnum;
import com.demo.malmo.chat.service.ChatService;
import com.demo.malmo.chat.vo.ChatRequest;
import com.demo.malmo.chat.vo.ChatStreamRequest;
import com.demo.malmo.chat.vo.ChatStreamResponse;
import com.demo.malmo.global.api.clova.service.ClovaService;
import com.demo.malmo.global.api.clova.vo.ClovaResponse;
import com.demo.malmo.global.api.clova.vo.HyperClovaRequest;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Slf4j
@Component
public class ChatFacade {


    private final ChatService chatService;
    private final ClovaService clovaService;

    @Transactional
    public ChatUserMessageEntity newChat(ChatRequest request, String userId) {

        var chatRoom = Optional.ofNullable(request.getRoomId())
            .map(chatService::getRoom)
            .orElseGet(() -> chatService.createRoom(userId, request.getCategory()));

        // 요청 사용자 대화 추가
        return chatService.createChatUserMessage(chatRoom, request.getMessage(), request.getRelyAiMessageId());

    }

    public Flux<ChatStreamResponse> chatStream(ChatStreamRequest request) {
        var chatUserMessage = chatService.getChatUserMessage(request.getChatPhaseId());

        var message = getMessage(request, chatUserMessage);
        log.info("message : {}", message);

        // 모자 ai 요청인 경우 ai 대화 추가
        var chatAiMessage = chatService.createChatAiMessage(ChatAiMessageEntity.builder()
            .chatRoomId(chatUserMessage.getChatRoomId())
            .chatUserMessageId(chatUserMessage.getId())
            .role(request.getRole())
            .build());

        return clovaService.getChatCompletion(new HyperClovaRequest(message), request.getRole())
            .publishOn(Schedulers.boundedElastic())
            .doOnNext(response -> { // clova 대화 결과 저장
                if (response.isResult()) {
                    if (request.getRole() == ChatRoleEnum.SUMMARY_ROOM_NAME) {
                        chatService.updateRoomName(chatUserMessage.getChatRoomId(), response.getMessage());
                    }
                    chatService.updateMessage(chatAiMessage, response.getMessage());

                }
            })
            .filter(ClovaResponse::isStream)        //  대화 결과 중 stream 만 반환
            .map(response -> new ChatStreamResponse(chatAiMessage, response.getMessage())
            );
    }

    /**
     * 다회차 페이즈, 하이퍼 클로바 이면서, 모자 ai 요청인 경우 이전 대화를 가져와서 합쳐서 반환
     */
    private String getMessage(ChatStreamRequest request, ChatUserMessageEntity entity) {
        if (request.getRole() == ChatRoleEnum.SUMMARY_ROOM_NAME) {
            return entity.getMessage();
        }

        if (request.getGptType() == GptTypeEnum.OPEN_AI) {
            return entity.getMessage();
        }

        if (entity.getPhase() <= 1) {
            return entity.getMessage();
        }

        var beforeMessage = chatService.getChatAiMessages(entity.getChatRoomId(), request.getRole())
            .stream()
            .map(ChatAiMessageEntity::squashMessage)
            .collect(Collectors.joining(" "));
        return beforeMessage + entity.getMessage();


    }
}
