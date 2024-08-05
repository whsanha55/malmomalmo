package com.demo.malmo.gpt;

import com.demo.malmo.gpt.service.GptService;
import com.demo.malmo.gpt.util.PromptUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
public class GptController {

    private final GptService gptService;

    @PostMapping(value = "/gpt", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getUser(@RequestBody String message) {
        return gptService.generate(PromptUtil.CLOVA_BLACK_CAP, message);
    }

}
