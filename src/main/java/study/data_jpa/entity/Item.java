package study.data_jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

//@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
//이걸 넣어서 create엔티티 어노테이션이 잘동작하도록 해야된다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {
//    @Id @GeneratedValue
//    private Long id;

    //private Long id;
    //이렇게 별도의 id어노테이션이 없이 존재하면 null을 못넣는다.
    //그래서 0으로 판단하게 된다.

    //만약 GeneratedValues를 안넣게 된다면?
    @Id
    private String id;

    //
    @CreatedDate
    private LocalDateTime createdDate;
    //새로운 객체 유무 파악을 위해서 생성일 객체를 만들고
    //isNew를 createdDate==null를 통해 풀어낼 수 있다.

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return createdDate==null;
    }
    //새로운 건 지 아닌 지 로직을 직접 만들어야 된다.


    public Item(String id) {
        this.id = id;
    }
}
//새로운 엔티티 구별
//Save함수는
//신규 데이터는 persist
//기존 데이터는 merge
//이렇게 하는데 이때 식별자가 객체일 경우 'null'로 판단
//식별자가 자바 기본 타입일 경우 '0'으로 판단.
