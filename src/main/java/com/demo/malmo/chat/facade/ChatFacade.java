package com.demo.malmo.chat.facade;

import com.demo.malmo.chat.entity.ChatUserMessageEntity;
import com.demo.malmo.chat.service.ChatService;
import com.demo.malmo.chat.vo.ChatRequest;
import com.demo.malmo.chat.vo.ChatStreamRequest;
import com.demo.malmo.chat.vo.ChatStreamResponse;
import com.demo.malmo.global.api.clova.service.ClovaService;
import com.demo.malmo.global.api.clova.vo.ClovaResponse;
import com.demo.malmo.global.api.clova.vo.HyperClovaRequest;
import jakarta.transaction.Transactional;
import java.util.Optional;
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

        log.info("chatRoom : {}", chatRoom);

        // 요청 사용자 대화 추가
        return chatService.createChatUserMessage(chatRoom, request.getMessage());

    }

    @Transactional
    public Flux<ChatStreamResponse> chatStream(ChatStreamRequest request) {
        var chatUserMessage = chatService.getChatUserMessage(request.getChatPhaseId());
        var chatRoom = chatService.getRoom(chatUserMessage.getChatRoomId());

        // 기존 대화 내용 조회
        var chatUserMessages = chatService.getChatUserMessages(chatRoom.getId());
        var hyperClovaRequest = new HyperClovaRequest(chatUserMessages, chatUserMessage.getMessage());

        return clovaService.getChatCompletion(hyperClovaRequest, request.getRole())
            .publishOn(Schedulers.boundedElastic())
            .doOnNext(response -> { // clova 대화 결과 저장
                if (response.isResult()) {
                    chatService.createChatAiMessage(chatUserMessage, response.getMessage(), request.getRole());
                }
            })
            .filter(ClovaResponse::isStream)        //  대화 결과 중 stream 만 반환
            .map(response -> ChatStreamResponse.builder()     // 대화 결과를 ChatStreamResponse 변환
                .roomId(chatUserMessage.getChatRoomId())
                .roomPhaseId(chatUserMessage.getId())
                .role(request.getRole())
                .message(response.getMessage())
                .build()
            );
    }

}
