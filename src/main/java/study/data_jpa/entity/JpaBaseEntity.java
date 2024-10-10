package study.data_jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
//Auditing
//엔티티를 생성, 변경할 때 변경한 사람과 시간을 추적하고 싶을 때 사용한다.
//디테일한 추적보단 기본적인 테이블을 만들 때
//등록일 수정일을 남겨야 한다.
//이걸 남겨야 운영 시 편리하다.
//이후 등록자/수정자 관련 정보도 넣는다
//이 데이터를 누가 등록했고 취소했는 지 로그인한 세션정보를 통해
//데이터를 등록해놓는다.
//등록일/수정일/등록자/수정자 관련 정보를 저장
//이러한 객체 세상에서는 상속이나 관계로 속성을 이어받아 사용할 수 있기
//때문에 이를 통해 자동화

@Getter
@MappedSuperclass
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    //이건 persist하기 전에 이벤트가 등작하도록 하는 것
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
//요즘은 id에 대해 색칠해줘서 this를 안써도 구별하기 편함
        //그래서 중복될 때만 사용
        //실무에서 updateDate에 데이터를 넣어놔서 맞춰놔야 나중에
        //업데이트에서 편리하다.
        //그리고 쿼리할 때 null이 존재하면 쿼리가 지저분하기 떄문에
        //기본값을 넣어주는 게 좋다.
    }

    @PreUpdate
    public void preUpdate(){
        updatedDate=LocalDateTime.now();
    }

    //사용할 엔티티에
    //extends JpaBaseEntity로 넣어주면 된다.
    //이때 컴파일을 하면 create엔티티를 할 경우
    //update와 craetedate정보가 없다.
    //이때 jpaBase는 속성만 상속받는 경우인데
    //이럴 경우 이 베이스 엔티티에
    //@MappedSuperclass 어노테이션을 통해 속성을 상속하여
    //테이블에서 사용할 수 있도록 하는 것
    //이렇게 하면 테이블 생성에 이 localDate정보가 들어가는 것을
    //확인할 수 있다.
}
