package com.demo.malmo.chat.request;

import com.demo.malmo.global.base.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class ChatAiRoomBookMarkResponse extends BaseResponse {

    @Schema(description = "요청 이후 북마크 여부", example = "true")
    boolean bookmarked;
}
