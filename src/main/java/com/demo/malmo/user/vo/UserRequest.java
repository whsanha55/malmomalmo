package com.demo.malmo.user.vo;

import com.demo.malmo.user.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserRequest {

    @Schema(description = "sns 고유 id", example = "12345")
    private String userId;
    @Schema(description = "email", example = "hello@naver.com")
    private String email;

    public UserEntity toEntity() {
        return UserEntity.builder()
            .userId(userId)
            .email(email)
            .build();
    }
}
