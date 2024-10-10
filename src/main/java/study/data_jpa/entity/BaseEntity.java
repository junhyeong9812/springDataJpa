package study.data_jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
//이렇게 엔티티 리스너 설정을 해줘야 엔티티가 저장되는 지 변경되는 지
//확인할 수 있다.
//하지만 이때 엔티티 리스너를 하나하나 넣기 싫으면 NETA-INF에 orm.xml을 생성하여
//엔티티 리스너 설정을 넣으면 이처럼 엔티티 리스너를 넣지 않아도 된다.
public class BaseEntity extends BaseTimeEntity{

//    //생성일
//    @CreatedDate
//    //이 어노테이션은 스프링데이터 프레임워크에 존재한다.
//    @Column(updatable = false)
//    private LocalDateTime createdDate;
//
//    //수정일
//    @LastModifiedDate
//    private LocalDateTime lastModifiedDate;

//    findMember.getCreatedDate() = 2024-10-10T17:07:07.734076
//    findMember.getUpdatedDate() = 2024-10-10T17:07:07.862975
//    이렇게 적용이 된 것을 알 수 있다.

    //등록자
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    //수정자
    @LastModifiedBy
    private String lastModifiedBy;

    //등록자와 수정자에 대해서는
    //스프링의 @Bean설정을 통해 잘 다뤄야 하는데


}
//기본적으로 날짜들은 기본으로 가져가지만 수정자와 작성자는 필요 없을 경우가
//많아서 시간만 있는 베이스엔티티와 시간과 작성자가 있는 베이스엔티티를 분리해서
//만든다.
