package com.demo.malmo.global.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {

    @Column
    protected boolean deleted;
    protected LocalDateTime deletedAt;

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @Column
    protected LocalDateTime updatedAt = LocalDateTime.now();

    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
