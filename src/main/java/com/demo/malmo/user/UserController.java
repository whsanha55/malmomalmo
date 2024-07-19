package com.demo.malmo.user;

import com.demo.malmo.user.entity.User;
import com.demo.malmo.user.service.UserService;
import com.demo.malmo.user.vo.UserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/user")
    public User getUser(String id) {
        return userService.getUser(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/user/sign-up")
    public void createUser(@Valid @RequestBody UserRequest request) {
        userService.createUser(request.toEntity());
    }
}
