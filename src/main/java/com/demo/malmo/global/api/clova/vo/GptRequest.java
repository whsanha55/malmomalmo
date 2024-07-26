package com.demo.malmo.global.api.clova.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Builder
@AllArgsConstructor
@Data
@Slf4j
@JsonInclude(Include.NON_DEFAULT)
public class GptRequest {

    @JsonProperty("user_query")
    String chatMessage;

}
