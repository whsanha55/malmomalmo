package com.demo.malmo.chat.repository;

import com.demo.malmo.chat.entity.ChatAiMessageEntity;
import com.demo.malmo.chat.enums.ChatRoleEnum;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ChatAiMessageRepository extends JpaRepository<ChatAiMessageEntity, Long> {

    @Query(value = """
        select cam
        from ChatAiMessage cam
        join fetch cam.chatRoom cr
        join fetch cam.chatUserMessage cum
        where cr.userId = :userId
        and cam.bookmarked = true
        and cam.deleted = false
        order by cam.bookmarkedAt desc, cam.id desc
        """)
    List<ChatAiMessageEntity> findBookmarked(String userId);

    @Query(value = """
        select cam
        from ChatAiMessage cam
        join fetch cam.chatUserMessage cum
        where cam.chatRoomId = :chatRoomId
        and cam.role = :role
        and cam.deleted = false
        """)
    List<ChatAiMessageEntity> findByChatRoomIdAndRole(Long chatRoomId, ChatRoleEnum role);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = """
        update ChatAiMessage cam
        set cam.deleted = true
        where cam.chatUserMessageId = :chatUserMessageId
        and cam.role = :role
        and cam.deleted = false
        """)
    int updateDeletedTrueByUserMessageIdAndRole(Long chatUserMessageId, ChatRoleEnum role);
}
