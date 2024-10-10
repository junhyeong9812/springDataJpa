package study.data_jpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long>,MemberRepositoryCustom {
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

//    List<Member> findByUsername(String username);
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

//    @Query(name = "Member.findByUsername")
    //@Query가 없어도 잘 동작하는데 이유는 관례로 Member.findByUserName
    //엔티티명에 .을찍고 메소드명으로 네임드쿼리를 우선적으로 찾고 있으면
    //실행하고 없으면 구현된 메소드이름으로 쿼리를 생성한다.
    //그래서 네임드 쿼리를 찾는게 우선순위라 이렇게 동작하는 것
    List<Member> findByUsername(@Param("username") String username);
    //jpql이 존재할때 네임드 파라미터를 넘겨야될 때 @Param어노테이션이 필요하다.
    //그러면 이 Param이름으로 매핑을 시켜준다.
    //JPA가 NamedQuery에서 Member.findByUsername이 이름을 가진 쿼리를 사용한다.
    //이때

//    이런 NamedQuery는 거의 사용하지 않는다.
//    쿼리가 엔티티에 있기도 하고 NamedQuery는 SpringJPA의 리포지토리 메소드에
    //쿼리를 지정할 수 있는데 이 기능이 훨씬 좋기 때문에 굳이
    //NamedQuery로 지정하지 않는다.

    //NamedQuery의 장점
    //em.createQuery는 오타가 있어도 쿼리가 문자열이라 컴파일 오류없이 애프리케이션이
    //실행되지만 실행되도 이 쿼리를 호출하면 에러가 나온다.
    //하지만 NamedQuery에 오타가 있으면 스프링이 자동으로 NamedQuery의
    //앱 로딩 시점에 파싱을 하고 문법 오류에 대해서 알려준다.
    //NameqQuery는 정적 쿼리이기 때문에 다 파싱해서 확인한다.

    //리포지토리 메소드에 쿼리 정의
    @Query("select m from Member m where m.username=:username and m.age = :age")
    List<Member> findUser(@Param("username") String username,@Param("age") int age);
    //이렇게 어노테이션 쿼리를 통해 바로 쿼리를 작성할 수 있다.
    //이 기능에 장점이 많은데
    //jpql과 이름또한 간략하게 사용할 수 있고 복잡한 JPQL을 통해 동작하도록 할 수 있다.
    //또한 이 기능의 장점에서 Qeury문 내부가 문자열이여도 로딩시점에 쿼리 에러가 나온다.
    //컴포넌트 스캔에서 @Query에 정의된 NamedQuery를 파싱을 통해 SQL을 미리 만들어놓는다.
    //그래서 파싱 과정에서 문법 오류를 찾아서 에러를 띄우는 것이다.
    //그리고 JPA 함수명이 길 때도 이렇게 쿼리로 작성하고
    //쿼리가 복잡할 경우 QueryDSL을 사용하는 게 좋다.

    //Query,값,DTO 조회
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    //DTO를 통한 값 조회
    @Query("select new study.data_jpa.dto.MemberDto(m.id,m.username,t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();
    //dto로 조회 시 쿼리에서 new 오퍼레이션이 필요하다.
    //이렇게 하기 때문에 생성자를 만든 것
    //이렇게 하면 DTO로 반환할 수 있다.


    //파라미터 바인딩
    //이름기반과 위치 기반이 존재하는데
    //? : 위치 기반
    //: : 이름 기반
    //위치 기반은 거의 사용하지 않는다.
    //왜냐면 코드 가독성이나 유지보수를 위해서 이름 기반을 자주 사용한다.
    //이전에 사용했던 @Param이다.

    //컬렉션 파라미터 바인딩
    @Query("select m from Member m where m.username in :names")
    //in절로 여러값을 조회할 때 사용한다.
    //이렇게 하면 SQL로 들어갈 때 ()안에 배열값을 다 넣어야 됬는데 이렇게 하면
    //컬렉션으로 in절이 자동으로 처리가 된다.
//    List<Member> findByNames(@Param("names") List<String> names);
    List<Member> findByNames(@Param("names") Collection<String> names);
    //이렇게 List가 아닌 Collection으로 하면 다른 컬렉션 타입도 받을 수 있으니
    //확장성으로 이렇게 사용하면 좋다.

    //Spring Jpa 반환타입
    //컬렉션
    List<Member> findListByUsername(String username);
    //단건
    Member findMemberByUsername(String username);
    //단건 Optional
    Optional<Member> findOptionalByUsername(String username);

//스프링 데이터 Jpa 페이징과 정렬
    //페이징을 하는 SQL을 가져다가 사용했었는데
    //spring jpa는 표준화를 시켜서 사용할 수 있도록 해놨다.

    //data.domain.Sort 정렬 기능
//    List<Member> findByUsername(String name,Sort sort);

    //data.domain.Pageable 페이징 기능(내부 Sort 포함)
//    Page<Member> findByAge(int age, Pageable pageable);
    //쿼리에 대한 조건이 Pageble로 들어가는 것

    //data.domain.Page 추가 count 쿼리 결과를 포함하는 페이징

    //data.domain.Slice 추가 Count 쿼리 없이 다음 페이지만 확인 가능
    //(내부적으로 limit+1)
    //스크롤이나 더보기 버튼을 통해 다음 페이징을 할 때 이걸 사용한다.
//    Slice<Member> findByAge(int age, Pageable pageable);
//    Slice<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용 안함
    //List(자바 컬렉션) :추가 Count 쿼리 없이 결과만 반환
//    List<Member> findByAgeList(int age, Pageable pageable); //count 쿼리 사용 안함


    //실무에서 Paging쿼리를 잘 안쓰는 이유는 토탈 카운트 쿼리가
    //DB의 모든 데이터를 카운트 해야되기 때문에
    //토탈 카운트 자체가 성능이 느리다.
    //짤라서 가져오는 페이지는 최적화가 쉽지만 토탈 카운트는
    //견적이 안나올 때가 많다.
    //그래서 토탈 카운트 쿼리를 잘 짜야 한다.
    //특히 Join이 많이 발생하게 되면
    //totalCount는 조인을 할 필요가 없는데
    //조인이 일어날 수 있다.
    //그래서 Count 쿼리를 잘 작성해야 된다.
    //Count 쿼리를 분리하는 방식이 존재한다.
    @Query(
            value = "select m from Member m left join m.team t"
//            ,countQuery = "select count(m.username) from Member m"
            ,countQuery = "select count(m.username) from Member m"
    )//이렇게 쿼리를 분리하고 나면
    Page<Member> findByAge(int age, Pageable pageable);
    //엔티티의 구조가 복잡할수록 모든 join을 가져와서 count 연산을 하기 때문에
    //느려질 수 있어서 이렇게 카운트 쿼리를 분리하여
//    select m1_0.member_id,m1_0.age,m1_0.team_id,m1_0.username
//    from member m1_0 order by m1_0.username desc
//    fetch first 3 rows only;

//    select count(m1_0.username) from member m1_0;
    //이렇게 쿼리가 분리될 수 있도록 해야 된다.
//    ,countQuery = "select count(m) from Member m"
    //이렇게 되면 Count 쿼리는 심플하기 때문에 이렇게 작성해서 사용하는 게 좋다.
    // 엔티티 구조가 복잡해질 경우 카운트 쿼리를 이처럼 분리하는 게 좋다.
    //소팅도 복잡해지면 Query내부에 Sorting조건을 넣어서 해결하면 된다.

//    Page<Member> findTop3ByAge(int age, Pageable pageable);
//    이런식으로 상위 3개만 가져오도록 설계도 가능하다.
    
    //벌크성 수정 쿼리
    //jpa는 엔티티를 가지고 와서 데이터를 변경하면 더티체킹을 통해
    //트랜젝션 끝나는 시점에 한건 한건씩 하는 것인데
    //이건 DB에 업데이트 쿼리를 한번에 전체 10%증가 같은 동작을
    //하는 것을 벌크성 수정 쿼리라 한다.
    //예시로 직원들 전체 연봉 10%상승 하는 것을 하나씩 하는 게 아닌
    //쿼리 하나로 전체 적용 되도록 하는 것
    //sql는 편하지만 엔티티는 별도로 분리가 되어 있다.

    @Modifying(clearAutomatically = true)
    //이 옵션을 통해 엔티티 메니져의 clear를 사용할 수 있다.
    //Modifying이 있어야 익스큐트업데이트처럼 동작한다.
    //만약 이 모디파이가 없으면 리져트리스트나 싱글 리져트리스트를 호출해버린다.
    @Query("update Member m set m.age=m.age+1 where m.age>=:age")
    public int bulkAgePlus(@Param("age") int age);
    //변경할 때는 모디파이가 필수!

    //fetch Join을 통한 N+1해결
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();
    //이렇게 fetch옵션을 사용하면 member를 조회하면 연관된 team을 한방쿼리로
    //가져온다.

    //EntityGraph
    @Override
    @EntityGraph(attributePaths = {"team"})
    //member와 team까지 조회하기 위한 엔티티 그래프
    List<Member> findAll();
//    select
//    m1_0.member_id,
//    m1_0.age,
//    t1_0.team_id,
//    t1_0.name,
//    m1_0.username
//            from
//    member m1_0
//    left join
//    team t1_0
//    on t1_0.team_id=m1_0.team_id

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

//    @EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all")
    //이렇게 NamedEntityGraph이름을 넣으면 이 네임드 엔티티 그래프가 실행된다.
    List<Member> findEntityGraphByUsername(@Param("username") String username);
    //이렇게 엔티티 그래프로 회원 조회를 통해 회원 데이터와 팀 데이터까지
    //한번에 가져온다.
    //왜냐하면 팀과 맴버를 자주 사용하기 때문에 이렇게 사용
//    select m1_0.member_id,m1_0.age,t1_0.team_id,t1_0.name,m1_0.username
//    from member m1_0 left join team t1_0 on t1_0.team_id=m1_0.team_id
//    where m1_0.username='member1';
//    이렇게 네임드 엔티티 그래프가 동작한 것을 확인할 수 있다.
    //이처럼 엔티티 그래프는 어트리뷰트로 직접 넣거나
    //네임드를 통해 간접적으로 넣을 수 있다.
//엔티티 그래프 어트리뷰트 패스나 간단할 경우 JPQL을 사용한다.
    //쿼리가 복잡해지면 find By같은 것은 안되기 떄문에
    //복잡할 때는 JPQL로 Fetch조인을 사용하고
    //쉬운 것들은 엔티티 그래프를 통해 값을 가져온다.

    //JPA Hint & Lock
    //jpa표준으로 JPA 쿼리에 대한 힌트를 준다.
    //JPA구현체에게 제공하는 힌트
    //이게 SQL에 날리는 힌트가 아닌 JPA객체에게 제공되는 힌트다.
    @QueryHints(value =@QueryHint( name="org.hibernate.readOnly",value = "true"))
    Member findReadOnlyByUsername(String username);
    //이렇게 해서 세팅을 하면 하이버네이트의 ReadOnly에
    //value는 true로 두개의 문자열인데
    //하이버네이트가 미리 지정해놓는 것

    
    //Lock이란?
    //select for update같은
    //select할때 데이터를 건드리지 않도록 Lock을 걸 수 있다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    //이렇게 하면 자카르타의 jpa의 기본적인 기능으로
    //write기능을 막는 것.
    List<Member> findLockByUsername(String username);


}
