package com.demo.malmo.chat.repository;

import com.demo.malmo.chat.entity.ChatAiMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatAiMessageRepository extends JpaRepository<ChatAiMessageEntity, Long> {

}
