package com.demo.malmo.global.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BaseResponse {

    @Schema(description = "응답 메시지", example = "SUCCESS")
    private String message = "SUCCESS";

}
