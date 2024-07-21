package com.demo.malmo.global.api.clova.service;

import com.demo.malmo.chat.enums.ChatRoleEnum;
import com.demo.malmo.global.api.clova.vo.ClovaResponse;
import com.demo.malmo.global.api.clova.vo.HyperClovaRequest;
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


    public Flux<ClovaResponse> getChatCompletion(HyperClovaRequest request, ChatRoleEnum role) {

        // TODO: 7/21/24 role 에 해당하는 각 모델에 대한 clova 대화 생성 필요
        return clovaWebClient.post()
            .uri("/testapp/v1/chat-completions/HCX-DASH-001")
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatusCode::isError, clientResponse -> {
                log.info("clientResponse : {}", clientResponse);
                var stringMono = clientResponse.bodyToMono(String.class);
                log.info("stringMono : {}", stringMono);
                return Mono.error(new RuntimeException(clientResponse.toString()));
            })
            .bodyToFlux(ClovaResponse.class);
    }


}
