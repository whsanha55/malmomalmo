package com.demo.malmo.global.base;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Data;

@Data
public class BaseResponse {

    @Schema(description = "응답 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    protected String uuid = UUID.randomUUID().toString();

}
