package com.demo.malmo.chat.repository;

import com.demo.malmo.chat.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

/*    @Query(value = """
        select cr
        from ChatRoom cr
        join fetch cr.chatMessages cm
        where cr.id = :id
        order by cm.createdAt desc
        """)
    @EntityGraph(attributePaths = "chatMessages", type = EntityGraphType.LOAD)*/
//    Optional<ChatRoomEntity> findById(Long id);
}
