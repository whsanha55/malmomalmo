package com.demo.malmo.chat.repository;

import com.demo.malmo.chat.entity.Chat;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface ChatRepository extends CrudRepository<Chat, String> {

}
