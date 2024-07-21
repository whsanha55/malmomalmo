package com.demo.malmo.chat;

import com.demo.malmo.chat.facade.ChatFacade;
import com.demo.malmo.chat.request.ChatRequest;
import com.demo.malmo.chat.request.ChatResponse;
import com.demo.malmo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ChatController {

    private final UserService userService;
    private final ChatFacade chatFacade;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> newChat(@RequestHeader("user-id") String userId, @RequestBody ChatRequest request) {
        userService.getUser(userId);
        return chatFacade.newChat(request, userId);
    }

}
