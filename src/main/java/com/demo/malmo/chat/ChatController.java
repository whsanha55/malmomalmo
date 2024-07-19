package com.demo.malmo.chat;

import com.demo.malmo.chat.entity.ChatContent;
import com.demo.malmo.chat.service.ChatService;
import com.demo.malmo.global.base.BaseController;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ChatController extends BaseController {

    private final ChatService chatService;

    @PostMapping("/chat")
    public Mono<?> newChat(@RequestBody String subject) {
        var userId = getUserId();
        var chat = chatService.createChat(userId, subject);
        return Mono.just(chat.getId());
    }

    @GetMapping("/chat")
    public Mono<?> getChat(String chatId) {
        var userId = getUserId();
        var chat = chatService.getChat(chatId);
        return Mono.just(chat);
    }

    @PostMapping("/chat/content")
    public Mono<?> addChatContent(@RequestBody ChatRequest request) {
        var userId = getUserId();
        var chat = chatService.addChatContent(request);
        return Mono.just(chat.getId());
    }

    @Data
    public static class ChatRequest {

        private String chatId;
        private String role;
        private String content;

        public ChatContent toEntity() {
            return ChatContent.builder()
                .chatId(chatId)
                .role(role)
                .content(content)
                .build();
        }

    }
}
