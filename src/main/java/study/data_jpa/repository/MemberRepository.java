package study.data_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.data_jpa.entity.Member;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
//    JpaRepository를 열어보면 내부에 타입과 ID의 식별자 타입을 받아서
//    이를 통해 Override로 함수에 받은 타입과 ID식별자 타입을 구현체로 전달하도록 되어있다.
//    많은 기능들이 있는데 JpaRepository의 springframework,date,jpa의 리포지토리를
//    인터페이스로 만들어놓은 것을 상속을 통해 CRUD를 제공하고 특화된 기능을 제공해준다.
//    이건 PagingAndSortingRepository를 상속하고 이건 data의 리포지토리를 상속받는다.
//    어떤DB는 방식이 비슷해서 공통 인터페이스를 제공해준다.
//    라이브러리를 가보면 PagingAndSortingRepository의 data.commom에 존재하는데
//    그 하부에 있는 JPA리포지토리는 spring-date-jpa에 존재한다.
//    그 상위에 CRUDRepository가 존재하는데 여기에 우리가 기본적으로 사용하는 메소드들이
//    제공이 된다.
//    그리고 최상위에는 Repository가 있는데 이건 마커인터페이스라 한다.
    //이건 스프링 데이터가 제공하는 리포지토리로 스프링 빈에서 클래스패스를 쉽게 해주는 것
//    기본적으로 CrudRepository는
//    Save/findOne/exists/count/delete를 제공하며
//    PagingAndSortingRepository는
//    findAll/findAll(Page<T>)를 제공해준다.
//    그리고 하위 스프링 데이터 JPA의 JpaRepository는
//    findAll/save/flush/saveAndFlush/deleteInBatch/deleteAllInBatch/getOne
//    함수를 제공해준다.
    //이때 findAll에 대해 페이징이나 Sort정렬을 제공해준다.

    List<Member> findByUsername(String username);
    //username에 대해 특화된 문제인데
    //이건 인터페이스이기 때문에 구현을 하기 위해 임플리먼츠를 하면
    //모든 기능을 다 구현해야 되기 때문에 상당히 힘들다.

//    List<Member> findByUsername(String username);
//    이렇게 쿼리로 하는걸 쿼리 메소드 기능이라 한다.
}
