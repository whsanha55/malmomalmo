package com.demo.malmo.chat.entity;

import com.demo.malmo.chat.enums.ChatRoleEnum;
import com.demo.malmo.global.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity(name = "ChatAiMessage")
@Builder
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatAiMessageEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, length = 256)
    private Long id;

    @Column(nullable = false)
    private Long chatRoomId;

    @Column(nullable = false)
    private Long chatUserMessageId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatRoleEnum role;

    @Column(nullable = false)
    @Builder.Default
    private String message = "";

    @Column(nullable = false)
    private boolean bookmarked;

    @Column
    private LocalDateTime bookmarkedAt;

    @ManyToOne(targetEntity = ChatRoomEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoomId", insertable = false, updatable = false)
    private ChatRoomEntity chatRoom;

    @ManyToOne(targetEntity = ChatUserMessageEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "chatUserMessageId", insertable = false, updatable = false)
    private ChatUserMessageEntity chatUserMessage;

    public void updateBookMark() {
        this.bookmarked = !this.bookmarked;
        this.bookmarkedAt = LocalDateTime.now();
    }


    public void updateMessage(String message) {
        this.message = message;
    }

    public String squashMessage() {
        return Optional.ofNullable(chatUserMessage)
            .map(ChatUserMessageEntity::getMessage)
            .orElse("") + this.message;
    }
}
