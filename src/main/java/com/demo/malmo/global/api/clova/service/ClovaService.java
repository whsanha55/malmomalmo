package com.demo.malmo.global.api.clova.service;

import com.demo.malmo.chat.enums.ChatRoleEnum;
import com.demo.malmo.chat.enums.GptTypeEnum;
import com.demo.malmo.global.api.clova.vo.GptRequest;
import com.demo.malmo.global.api.clova.vo.GptResponse;
import com.demo.malmo.global.exception.BaseException;
import com.demo.malmo.gpt.service.GptService;
import com.demo.malmo.gpt.util.PromptUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class ClovaService {

    private final WebClient clovaWebClient;
    private final ObjectMapper objectMapper;
    private final GptService gptService;


    public Flux<GptResponse> getChatCompletion(GptRequest request, ChatRoleEnum role, GptTypeEnum gptType, int phase) {
        log.info("request : {}", request);

        // open-ai 요청인 경우 파이썬 서버로 보내지 않고 직접 처리
        if (gptType == GptTypeEnum.OPEN_AI) {
            var prompt = PromptUtil.prompt(role, phase);
            return gptService.generate(prompt, request.getChatMessage())
                .map(token -> GptResponse.builder()
                    .result(token)
                    .build());
        }

        return clovaWebClient.post()
            .uri(getUrl(role, gptType, phase))
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatusCode::isError, clientResponse -> {
                log.info("clientResponse : {}", clientResponse);
                var stringMono = clientResponse.bodyToMono(String.class);
                log.info("stringMono : {}", stringMono);
                return Mono.error(new RuntimeException(clientResponse.toString()));
            })
            .bodyToFlux(String.class)
            .doOnNext(response -> log.info("response : {}", response))
            .map(response -> {
                try {
                    return objectMapper.readValue(response, GptResponse.class);
                } catch (JsonProcessingException e) {
                    log.error("", e);
                    return new GptResponse();
                }
            })
            .flatMap(r -> Flux.fromArray(Arrays.stream(r.getResult().split(" "))
                .map(token -> GptResponse.builder()
                    .result(token + " ")
                    .build())
                .toArray(GptResponse[]::new)))
            .delayElements(Duration.ofMillis(100L))
            ;
    }

    private String getUrl(ChatRoleEnum role, GptTypeEnum gptType, int phase) {
        if (gptType == GptTypeEnum.HYPER_CLOVA) {
            return switch (role) {  // 클로바
                case WHITE_HAT -> "white-cap-first-brainstorming/";
                case BLACK_HAT -> "black-cap-brainstorming/";
                case GREEN_HAT -> "green-cap-brainstorming/";
                case YELLOW_HAT -> "yellow-cap-brainstorming/";
                case RED_HAT -> "red-cap-brainstorming/";
                case BLUE_HAT_BEGIN -> "blue-start/";
                case BLUE_HAT -> "gpt-blue-total-summary/";
                case SUMMARY -> "total-summary/";
                case SUMMARY_ROOM_NAME -> "clova-title-summary/";
                default -> throw new BaseException("Invalid role: " + role);
            };
        } else {
            return switch (role) {  // gpt-4 mini
                case WHITE_HAT -> "white-cap-second-brainstorming/";
                case BLACK_HAT -> "gpt-black-brainstorming/";
                case GREEN_HAT -> "gpt-green-brainstorming/";
                case YELLOW_HAT -> "gpt-yellow-brainstorming/";
                case RED_HAT -> "gpt-red-brainstorming/";
                case BLUE_HAT_BEGIN -> "blue-start-gpt/";
                case BLUE_HAT -> switch (phase) {
                    case 1 -> "blue-start-gpt/";
                    case 2 -> "gpt_2_start_blue/";
                    default -> "gpt_3_start_blue/";
                };
                case SUMMARY -> "total-summary/";
                case SUMMARY_ROOM_NAME -> "gpt-title-summary/";
                default -> throw new BaseException("Invalid role: " + role);
            };
        }

    }

}
