package com.demo.malmo.user.vo;

import com.demo.malmo.user.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotNull(message = "ID cannot be null")
    @Size(min = 5, max = 13, message = "ID must be between 5 and 13 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "ID must contain only letters and numbers")
    private String id;
    @NotNull
    private String name;

    public User toEntity() {
        return User.builder()
            .id(id)
            .name(name)
            .build();
    }
}
