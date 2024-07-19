package com.demo.malmo.user.service;

import com.demo.malmo.global.exception.PoliException;
import com.demo.malmo.user.entity.User;
import com.demo.malmo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public User getUser(String id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new PoliException("user not found"));
    }

    public boolean isExist(String id) {
        return userRepository.findById(id).isPresent();
    }

    @Transactional
    public void createUser(User user) {
        if (isExist(user.getId())) {
            throw new PoliException("id가 이미 존재합니다.");
        }

        userRepository.save(user);
    }
}
