package study.data_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.data_jpa.entity.Team;

public interface TeamRepository extends JpaRepository<Team,Long> {
    //jpa리포지토리를 받으면 그 구현체를 springJpa가 만들어서 상속시킨다.
    //리포지토리 어노테이션을 생략해도 된다.
    //컴포넌트 스캔에 걸리는 이유는 JpaRepository을 보고 해준다.
    //@Repository는 컴포넌트 스캔과 JPA의 예외를 스프링에서 공통적으로 처리할 수 있는 예외로
    //처리할 수 있게 해준다.
}
