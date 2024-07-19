package com.demo.malmo.user;

import com.demo.malmo.global.config.WebclientConfig;
import com.demo.malmo.global.config.vo.ChatMessage;
import com.demo.malmo.global.config.vo.ChatRequest;
import com.demo.malmo.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@Slf4j
public class TestController {

    private final UserService userService;
    private final WebclientConfig webclientConfig;

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<?> getUser(String content) {
        ChatRequest request = new ChatRequest();
        request.setModel("gpt-3.5-turbo-1106");
        request.setMessages(List.of(new ChatMessage(content)));
        request.setTemperature(1);
        request.setMax_tokens(256);
        request.setTop_p(1);
        request.setFrequency_penalty(0);
        request.setPresence_penalty(0);
        return webclientConfig.getChatCompletion(request);

    }
}
