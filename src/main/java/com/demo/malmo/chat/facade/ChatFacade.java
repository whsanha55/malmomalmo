package com.demo.malmo.chat.facade;

import com.demo.malmo.chat.enums.ChatRoleEnum;
import com.demo.malmo.chat.request.ChatRequest;
import com.demo.malmo.chat.request.ChatResponse;
import com.demo.malmo.chat.service.ChatService;
import com.demo.malmo.global.api.clova.service.ClovaService;
import com.demo.malmo.global.api.clova.vo.ClovaResponse;
import com.demo.malmo.global.api.clova.vo.HyperClovaRequest;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Slf4j
@Component
public class ChatFacade {


    private final ChatService chatService;
    private final ClovaService clovaService;

    @Transactional
    public Flux<ChatResponse> newChat(ChatRequest request, String userId) {

        var chatRoom = Optional.ofNullable(request.getRoomId())
            .map(chatService::getRoom)
            .orElseGet(() -> chatService.createRoom(userId, request.getCategory()));

        log.info("chatRoom : {}", chatRoom);

        // 기존 대화 내용 조회
        var chatUserMessages = chatService.getChatUserMessages(chatRoom.getId());
        log.info("chatUserMessages : {}", chatUserMessages);

        // 요청 사용자 대화 추가
        var chatUserMessage = chatService.createChatUserMessage(chatRoom, request.getMessage());

        var fluxStream = Arrays.stream(ChatRoleEnum.values())
            .map(
                // 각 모자마다 clova 대화 생성
                role -> clovaService.getChatCompletion(new HyperClovaRequest(chatUserMessages, request.getMessage()), role)
                .doOnNext(response -> { // clova 대화 결과 저장
                    if (response.isResult()) {
                        chatService.createChatAiMessage(chatUserMessage, response.getMessage(), role);
                    }
                })
                .filter(ClovaResponse::isStream)        // clova 대화 결과 중 stream만 반환
                .map(response -> ChatResponse.builder()     // clova 대화 결과를 ChatResponse로 변환
                    .role(role)
                    .message(response.getMessage())
                    .build()
                )

            );
        return Flux.merge(Flux.fromStream(fluxStream)); // 모든 clova 대화 결과를 Flux로 반환

    }

}
