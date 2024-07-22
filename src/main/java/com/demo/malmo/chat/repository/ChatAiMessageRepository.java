package com.demo.malmo.chat.repository;

import com.demo.malmo.chat.entity.ChatAiMessageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatAiMessageRepository extends JpaRepository<ChatAiMessageEntity, Long> {

    @Query(value = """
        select cam
        from ChatAiMessage cam
        join fetch cam.chatRoom cr
        where cr.userId = :userId
        and cam.bookmarked = true
        order by cam.id desc
        """)
    List<ChatAiMessageEntity> findBookmarked(String userId);
}
