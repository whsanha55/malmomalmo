package com.demo.malmo.chat.repository;

import com.demo.malmo.chat.entity.ChatUserMessageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatUserMessageRepository extends JpaRepository<ChatUserMessageEntity, Long> {

    @Query("""
            select cum
            from ChatUserMessage cum
            join fetch cum.chatAiMessages cam
            where cum.chatRoomId = :chatRoomId
            and cam.deleted = false
            order by cum.createdAt desc
        """)
    List<ChatUserMessageEntity> findByChatRoomId(Long chatRoomId);
}
