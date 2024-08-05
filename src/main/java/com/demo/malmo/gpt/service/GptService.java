package com.demo.malmo.gpt.service;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class GptService {

    private final OpenAiStreamingChatModel openAiStreamingChatModel;

    public Flux<String> generate(String systemMessage, String userMessage) {

        var messages = List.of(
            SystemMessage.from(systemMessage),
            UserMessage.from(userMessage)
        );
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        openAiStreamingChatModel.generate(messages, new StreamingResponseHandler<>() {
            @Override
            public void onNext(String token) {
                sink.tryEmitNext(token).orThrow();
            }

            @Override
            public void onError(Throwable error) {
                sink.tryEmitError(error).orThrow();
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                sink.tryEmitComplete().orThrow();
            }
        });

        return sink.asFlux();
    }

}
