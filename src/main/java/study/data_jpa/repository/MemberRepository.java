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
    //검색 조건이 들어간 문제는 공통으로 못만드는데
    //이걸 해결한 것이 쿼리 메소드라는 기능이다.

    //순수 JPA에서 만든
    //findByUserNameAndAgeGreaterThen이 함수의 역할을
    List<Member> findByUsernameAndAgeGreaterThan(String username,int age);
    //이렇게만 해도 힘들게 만든 코드를 JPA가 만들어주는 것을 알 수 있다.

    //이때 가장 심각한 문제는 And문으로 조건을  더 넣는다면?
    //이름이 너무 길어진다.
    //그래서 보통 두개정도의 변수는 이렇게 푸는데
    //두개 이상일 경우 JPQL을 통해 구성한다.
    //이때 By는 필수 By 뒤에 아무것도 없으면 전체 조회로 인식한다.
    List<Member> findHelloBy();
//    - 조회: find…By ,read…By ,query…By get…By,
//    - COUNT: count…By 반환타입 long
//    - EXISTS: exists…By 반환타입 boolean
//    - 삭제: delete…By, remove…By 반환타입 long
//    - DISTINCT: findDistinct, findMemberDistinctBy
//    - LIMIT: findFirst3, findFirst, findTop, findTop3
    List<Member> findTop3HelloBy();

}
