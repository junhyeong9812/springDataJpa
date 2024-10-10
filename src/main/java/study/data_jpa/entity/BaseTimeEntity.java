package study.data_jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {
    //생성일
    @CreatedDate
    //이 어노테이션은 스프링데이터 프레임워크에 존재한다.
    @Column(updatable = false)
    private LocalDateTime createdDate;

    //수정일
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
