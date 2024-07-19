package com.demo.malmo.user.vo;

import com.demo.malmo.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserRequest {

    @Schema(description = "sns 고유 id", example = "12345")
    private String userId;
    @Schema(description = "email", example = "hello@naver.com")
    private String email;

    public User toEntity() {
        return User.builder()
            .userId(userId)
            .email(email)
            .build();
    }
}
