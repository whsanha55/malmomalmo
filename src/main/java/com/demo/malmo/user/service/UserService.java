package com.demo.malmo.user.service;

import com.demo.malmo.global.exception.BaseException;
import com.demo.malmo.user.entity.UserEntity;
import com.demo.malmo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserEntity getUser(String id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new BaseException("user not found", HttpStatus.UNAUTHORIZED));
    }

    public boolean isExist(String id) {
        return userRepository.findById(id).isPresent();
    }

    @Transactional
    public UserEntity createUser(UserEntity user) {
        return userRepository.save(user);
    }
}
