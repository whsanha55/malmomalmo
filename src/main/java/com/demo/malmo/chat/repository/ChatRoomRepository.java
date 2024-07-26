package com.demo.malmo.chat.repository;

import com.demo.malmo.chat.entity.ChatRoomEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    @Query(value = """
        select cr
        from ChatRoom cr
        join fetch cr.chatUserMessages cu
        where cr.userId = :userId
        and cr.deleted = false
        order by cr.id desc
        """)
    List<ChatRoomEntity> findByUserIdOrderByIdDesc(String userId);


}
