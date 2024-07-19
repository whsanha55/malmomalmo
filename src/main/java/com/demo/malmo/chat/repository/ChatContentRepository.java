package com.demo.malmo.chat.repository;

import com.demo.malmo.chat.entity.ChatContent;
import java.util.List;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface ChatContentRepository extends CrudRepository<ChatContent, String> {

    List<ChatContent> findByChatId(String chatId);
}
