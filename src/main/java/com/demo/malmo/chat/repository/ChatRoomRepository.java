package com.demo.malmo.chat.repository;

import com.demo.malmo.chat.entity.ChatRoomEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    List<ChatRoomEntity> findByUserIdOrderByIdDesc(String userId);


}
