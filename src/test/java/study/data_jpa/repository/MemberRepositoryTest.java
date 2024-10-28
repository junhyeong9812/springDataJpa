package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember(){
        System.out.println("memberRepository.getClass() = " + memberRepository.getClass());
        Member member= new Member("memberA");
        memberRepository.save(member);
        //Save라는 함수가 인터페이스에 상속만 했는데 기능이 제공이 되는 것을 확인할 수 있다.
        //JpaRepository도 인터페이스인데 save기능이 제공되고
        //findById 기능도 제공이 되는데 이렇게 인터페이스만 있으면 기본적인
        //코드들은 JPA리포지토리가 만들어서 제공해준다.
        //기존의JPA코드와 거의 같지만 직접 코드를 작성하지 않아도 된다는 장점이 존재한다.

        Member savedMember =memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();
        //데이터의 존재 유무를 모르기 때문에 Optional로 제공하기에 .get을 통해
        //데이터를 꺼내야 한다. 하지만 null이면 NosuchElent익셉션이 터진다.
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);


    }

    @Test
    public void bassicCRUD(){
        Member member1=new Member("member1");
        Member member2=new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //유저 업데이트 확인
        findMember1.setUsername("member!!!!!!");
        //DB 쿼리 결과
//        AGE  	MEMBER_ID  	TEAM_ID  	USERNAME
//        0	    1	        null	member!!!!!!
//        0	    2	        null	member2
//        이렇게 이름이 변경된 것을 확인할 수 있다.
//        2024-09-28T21:38:23.244+09:00  INFO 17396 --- [data-jpa] [           main] p6spy                                    : #1727527103244 | took 0ms | statement | connection 3| url jdbc:h2:tcp://localhost/~/datajpa
//        update member set age=?,team_id=?,username=? where member_id=?
//        update member set age=0,team_id=NULL,username='member!!!!!!' where member_id=1;
//        이렇게 쿼리로 update문이 나가는 것을 알 수 있다.

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

    }
    //이렇게 인터페이스로 구현체로 만들어도 정상적으로 동작하는 것을 확인할 수 있다.
    @Test
    public void findByUserNameAndAgeGreaterThan(){
        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("aaa", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("aaa", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("aaa");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }
    //이게 공통 인터페이스에서 메서드 이름으로 생성해주는 기술이다.
    //관례를 가지고 동작하는데
    //username과 and조건이고 내부 객체 명이 이퀄로 동작하도록 하며
    //Age에 대해 GreaterThan조건을 추가시키는 것이다.
//    https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
    //위에서 각종 조건을 볼 수 있다.
    
    @Test
    public void findHelloBy(){
        List<Member> helloBy = memberRepository.findHelloBy();
    }
//    .SQL                        :
//    select
//    m1_0.member_id,
//    m1_0.age,
//    m1_0.team_id,
//    m1_0.username
//            from
//    member m1_0
//    이렇게 쿼리가 전체 조회로 나가는 것을 확인할 수 있다.
@Test
public void findTop3HelloBy(){
    List<Member> helloBy = memberRepository.findTop3HelloBy();
}
//.SQL                        :
//    select
//    m1_0.member_id,
//    m1_0.age,
//    m1_0.team_id,
//    m1_0.username
//            from
//    member m1_0
//    fetch
//    first ? rows only
    //실체 쿼리를 보면 rows
//select m1_0.member_id,m1_0.age,m1_0.team_id,m1_0.username from member m1_0 fetch first 3 rows only;
//    이렇게 쿼리가 나간 것을 알 수 있다.
    //노가다성 쿼리들은 이렇게 Jpa리포지토리를 통해 간편하게 만들고
    //복잡한 쿼리는 NamedQuery나 QueryDSL로 풀어내는 것이다.

    //또한 이런 엔티티와 JPA를 활용하는 장점으로는 인터페이스에 정의된 메서드 이름이 항상
    //엔티티와 같아야 한다.
    //엔티티의 변수명이 변경되면 메서드 이름도 바꾸도록 컴파일 에러가 나도록 해준다.
    //오류는 컴파일 < 시작전 에러 < 실제 운영에러 순서대로 동작하는 것


    @Test
    public void testNamedQuery(){
        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("bbb", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        List<Member> result = memberRepository.findByUsername("aaa");
        Member findMember=result.get(0);
        assertThat(findMember).isEqualTo(member1);
        //테스트가 정상적으로 성공한 것을 볼 수 있다. 하지만 이걸 구현하는게 상당히 번거롭다.

    }

    @Test
    public void testQuery(){
        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("bbb", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        List<Member> result = memberRepository.findUser("aaa",10);
        Member findMember=result.get(0);
        assertThat(findMember).isEqualTo(member1);
        //테스트가 정상적으로 성공한 것을 볼 수 있다. 하지만 이걸 구현하는게 상당히 번거롭다.

    }

    @Test
    public void findUsernameList(){
        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("bbb", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s="+s);
        }
    //s=aaa
    //s=bbb
    }

    @Test
    public void findMemberDto(){

        Team team =new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("aaa", 10);
        member1.setTeam(team);
        memberRepository.save(member1);

        //DB에서 조회 시
        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
        //dto = MemberDto(id=1, username=aaa, teamName=teamA)
        //이렇게 출력되는 것을 확인할 수 있따.

    }
    //컬렉션 파라미터 바인딩
    @Test
    public void findByNames(){
        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("bbb", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);
        List<Member> usernameList = memberRepository.findByNames(Arrays.asList("aaa","bbb"));
        for (Member m : usernameList) {
            System.out.println("m="+m);
        }
//        select m1_0.member_id,m1_0.age,m1_0.team_id,m1_0.username from
//        member m1_0 where m1_0.username in ('aaa','bbb');
//        m=Member(id=1, username=aaa, age=10)
//        m=Member(id=2, username=bbb, age=20)
    }
    //위치기반 파라미터바인딩은 쓰지않는 게 좋다.

    //변환타입
    @Test
    public void returnType(){
        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("bbb", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //컬렉션
        List<Member> memberList = memberRepository.findListByUsername("aaa");
        System.out.println("memberList = " + memberList.get(0));
        //파라미터를 막 넣어서 없는 데이터일 경우에는 null이 아니가
        //empty Collection을 반환해준다.
        //빈 컬렉션을 반환해주는데 이게 기본적인 jpa기능이다.
        //그래서 result=null같은 코드는 사용하면 안된다.
        //단건 조회로 받을 경우 member

        //단건 조회
        Member member = memberRepository.findMemberByUsername("aaa");
        System.out.println("member = " + member);
        //하지만 단건 조회를 할 경우
        //없으면 empty collection이 아니라
        //결과가 null로 나온다.
        //jpa는 싱글값에 대해서는 없으면 notResultExp이 나오는데
        //spring data jpa는 자기가 감싸서 Null로 반환시켜준다.




        //Optional조회
        Optional<Member> OptionalMember = memberRepository.findOptionalByUsername("aaa");
        System.out.println("OptionalMember = " + OptionalMember.get());
        //하지만 자바8부터는 Optional을 사용해서
        //null일 경우 Optional.empty가 나온다.
        //DB에 조회할 경우 데이터가 있을 지 없을 지 모를 경우에는
        //optional을 사용하는 게 좋다.
        //단 여기서 단건 조회인데 여러개의 값이 넘어오면 예외가 터진다.
        //IncorrectResultSize에러가 나온다.
        //NoUniqueResult가 터지면 spring이 IncorrectResultSize로 변환해서
        //익셉셥이 나오는 것이다.
        //이렇게 하는 이유는 jpa에러보다 Spring에러에 대해서 터지는 게
        //서비스 계층의 클라이언트 코드들은 Spring에 추상화된 에러에 의존하는 게
        //하부의 다른 JDBC나 바꿔도 데이터가 안맞으면 같은 에러를 띄우게 할 수있다.
        //그래서 클라이언트 코드를 바꿀 필요가 없다.
        //jpa예외가 아닌 SPring예외로 하여 호환이 좋게 하는 것.

        //Option이나 Stream, Future,CompletableFuture, page,Slice,( Mono,Flux(
        //React))기술같은
        // 반환타입도 존재한다.

        //Jpa 페이징과 정렬


    }
    //Spring Jpa 페이징과 정렬
    @Test
    public void paging(){
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));

        //page =1 offset =0 limit 10 ,page =2 offset=11 limit=20
//        int age =10;
//        int offset=0;
//        int limit =3;
        //Pageable을 사용할 때는
        PageRequest pageRequest =
                PageRequest
                        .of(0, 3
                                , Sort.by(Sort.Direction.DESC, "username"));
        //page를 0부터 시작,페이지의 크기도 넣는다.
        int age=10;

        //when
        Page<Member> page = memberRepository.findByAge(age,pageRequest);
        //이렇게 하면 PageRequest의 부모 인터페이스가 Pageable이기 때문에
        //PageRequest를 넘겨도 된다.
        //PageRequest이걸 넘길 때 페이징 하는 쿼리는 되고
        //반환 타입에 따라서 토탈 카운트를 날릴 지 안날릴 지 결정이 된다.
        //page객체에 가보면 Slice는 토탈 카운트를 가져오지 않고
        //다음 페이지 유무를 통해 확인 하는 것
//        Slice<Member> page = memberRepository.findByAge(age,pageRequest);
        //이때 Slice는 3개가 PageSize라면 총 4개를 요청해서 다음 페이지 유무를
        //판단한다.
//        select m1_0.member_id,m1_0.age,m1_0.team_id,m1_0.username
//        from member m1_0 where m1_0.age=10
//        order by m1_0.username desc fetch
//        first 4 rows only;
//        member = Member(id=5, username=member5, age=10)
//        member = Member(id=4, username=member4, age=10)
//        member = Member(id=3, username=member3, age=10)
        //이렇게 총 4개를 가져온다.

        //그리고 반환 타입을 page라고 받으면
        //Page 내에 TotalCount 쿼리까지 같이 날린다.

        //또한 데이터만 원하는 페이지 사이즈만 가져오고 싶을 경우에는
//        List<Member> pageList = memberRepository.findByAgeList(age,pageRequest);
//        이렇게 하면 size만큼만 가져올 수 있다.

        //하지만 page<Member>의 Member엔티티는 DTO로 변경해서 내보내야 한다.
        //이부분 매우 중요
        //Page<Member>를 쉽게 DTO로 변경할 수있는 방법이 있는데
        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        //map은 내부의 데이터를 변경해서 넣는 것
        //이렇게 map을 통해 변경된 DTO로 변경한다.
//        그럼 이 map은 DTO로 변경했으니 이대로 데이터를 내보면 된다.
        //또한 Page도 Json타입으로 반환되기 때문에
        //Page타입으로 APi로 보내주면 json으로 데이터를 확인할 수 있다.



        //then
        List<Member> content =page.getContent();
        //실제 데이터 3개를 꺼낼 때는 getContent()를 통해
        //꺼낼 수 있다.
//        long totalCount = page.getTotalElements();
        //토탈카운트도 가져온다.
        for (Member member : content) {
            System.out.println("member = " + member);
        }
//        System.out.println("totalCount = " + totalCount);
//        페이징 쿼리
//        select m1_0.member_id,m1_0.age,m1_0.team_id,m1_0.username from member m1_0 where m1_0.age=10 order by m1_0.username desc fetch first 3 rows only;
//        토탈 카운트
//        select count(m1_0.member_id) from member m1_0 where m1_0.age=10;

//        member = Member(id=5, username=member5, age=10)
//        member = Member(id=4, username=member4, age=10)
//        member = Member(id=3, username=member3, age=10)
//        totalCount = 5
        //이렇게 나가는 것을 알 수 있다.
        //이때 offset이 없는 이유는 0번째 페이지이기 때문

        //when
        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
        //이걸 통해 다음페이지 이전 페이지 유무를 알 수 있는 것이다.
    }
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


    //벌크성 수정 쿼리
    @Test
    public void bulkUpdate(){
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",19));
        memberRepository.save(new Member("member3",20));
        memberRepository.save(new Member("member4",21));
        memberRepository.save(new Member("member5",40));
        em.flush();
        //when
        int resultCount = memberRepository.bulkAgePlus(20);
        //영속성 컨텍스트가 데이터의 변경사항을 모르기 때문에
        //벌크 연산 이후에는 영속성 컨텍스트는 flush랑 clear로 날리고
        //다시 영속성 컨텍스트로 객체를 받아야 한다.
        //flush랑 clear를 하기 위해서는
        //persistContext를 통해 엔티티 메니져를 주입하여 사용
//        em.clear();
        //하지만 Spring Data Jpa에서는 @Modifying(clearAutomatically = true)
        //를 통해 clear를 사용 가능

        List<Member> result = memberRepository.findByUsername("member5");
        Member member = result.get(0);
        System.out.println("member = " + member);
        //이때 member는 40이다.
//        member = Member(id=5, username=member5, age=40)
        //하지만 DB에는 41로 반영이 되어 있어서
        //데이터가 일치하지 않는다.
        //flush와 clear를 하면
//        member = Member(id=5, username=member5, age=41)
//        age가 41로 되어진 것을 확인할수 있다.
        //그래서 벌크 연산 후에는 반드시 클리어와 플러시를 해주거나
        //그냥 트랜젝션을 종료시키는 게 좋다.

        //then
        assertThat(resultCount).isEqualTo(3);
//        update member m1_0 set age=(m1_0.age+1) where m1_0.age>=20;
        //실제 DB로 보면 20보다 높은 모든 값이 1씩 더해져있는 것을 알 수 있다.
//        update member m1_0 set age=(m1_0.age+1) where m1_0.age>=20;
//    위처럼 쿼리가 나오지만
        //모디파이를 뺀다면
        //.InvalidDataAccessApiUsageException:이 에러가 나온다.
        //하지만 이떄 jpa에서 이런 벌크성업데이트를 하면
        //엔티티가 관리가 되는데 벌크 연산은 이런 영속성 컨텍스트를 무시한다.
        //그래서 여기서 저장할 때 영속성 컨텍스트에 들어있는 상태로
        //벌크 연산을 하면 데이터가 서로 안맞을 수 있다.
        //위의  memberRepository.findByUsername를 통해 확인

        //중요
//        가장 좋은 것은 벌크 연산만 실행하고 끝내고 다시 조회해서 동작하는 게
//        더 좋다. Spring data jpa랑 마이바티스랑 jdbc를 같이 사용할 때
        //쿼리를 직접 날리는 것은 jpa가 인식하지 못하고
        //영속성 컨텍스트가 관리가 안된다.
        //그래서 영속성 컨텍스트와 맞지 않는다.
//        그래서 이 경우에는 flush나 clear작업이 필요하다.
    }

    //@EntityGraph
    //EntityGraph를 알기 위해서는 Fetch join에 대해서 알아야 한다.
    //Lazy의 지연로딩의 N+1문제를 해결하기 위한 방식
    @Test
    public void findMemberLazy(){
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);
        //이때 member에서 team은 다대일 관계이며 fetch타입을 lazy로
        //해놨는데 이때 member를 조회할 때 team의 데이터가 필요가 없다면
        //프록시 객체로 채워놓는 lazy방식
        em.flush();
        em.clear();
        //이후 저장하고 영속성 컨텍스트를 비운다.

        //when
        List<Member> members = memberRepository.findAll();
        //이때 Member의 객체 정보만 가져오고 연관관계 부분은 Proxy로 채워넣는다.
        //Proxy라는 가짜 객체를 만들어 놓는다.
        //보통 이러한 동작을 프록시를 초기화한다고 표현한다.
        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
        }
        //이렇게만 테스트를 돌리면 select로 
//        select m1_0.member_id,m1_0.age,m1_0.team_id,m1_0.username from member m1_0;
//        member.getUsername() = member1
//        member.getUsername() = member2
//        이렇게 단순하게 member만 조회하지만
        for (Member member : members) {
            System.out.println("member.getTeam() = " + member.getTeam().getClass());
        }
        //이렇게 getTeam의 클래스를 출력하면
//        member.getTeam() = class study.data_jpa.entity.Team$HibernateProxy$HE9saTJW
//        member.getTeam() = class study.data_jpa.entity.Team$HibernateProxy$HE9saTJW
        //프록시 객체인 것을 알 수 있다.

        for (Member member : members) {
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
        
        //이렇게 team을 조회하게 되면 이때 프록시로 있던 객체를 조회하게 되는데
//        select t1_0.team_id,t1_0.name from team t1_0 where t1_0.team_id=1;
//        member.getTeam().getName() = teamA
//        select t1_0.team_id,t1_0.name from team t1_0 where t1_0.team_id=2;
//        member.getTeam().getName() = teamB
        //이렇게 루프를 돌면서 값을 찍는 것을 알 수 있다.
        //하지만 이때 member의 수만큼 select쿼리가 더 날라가기 때문에
        //이러한 문제를 N+1이라 한다.
        //이렇게 조회할 때마다 쿼리가 이리 많이 나가면 서버에 부하를 줄 수 있다.
        //DB입장에서 1번 쿼리에 추가로 수십개의 쿼리가 더 나가서 통신을 하게 된다.
        //이러한 문제를 fetch join을 통해 해결한다.

        //확인을 위한 영속성 컨텍스트 삭제
        em.clear();

        //fetch join을 해보면
        List<Member> memberFetchJoin = memberRepository.findMemberFetchJoin();
//        select m1_0.member_id,m1_0.age,t1_0.team_id,t1_0.name,m1_0.username
//        from member m1_0 left join
//        team t1_0 on t1_0.team_id=m1_0.team_id;
        //left join을 통해 member의 정보와 team의 정보를 한번에
        //다 가져와서 member에 있는 team객체까지 생성해서 넣어주는 것
        //프록시가 아닌 실제 객체를 넣는 것
        for (Member member : memberFetchJoin) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
        //이렇게 확인해보면
//        member.getUsername() = member2
//        member.getTeam().getClass() = class study.data_jpa.entity.Team
//        member.getTeam().getName() = teamB
//        getTeam의 class가 실제 엔티티가 들어가있는 것을 볼 수 있다.
//        fetch join은 연관관계가 있는 것을 한번에 가져오는데
//        연관관계가 있는 것들에 대해서 객체 그래프라 표현하며
//        연관된 것을 조인하며 다 가져오는 것
//        db의 Join은 진짜 join만 하지만
//        fetch join은 select절에 그 내용을 다 넣어주는 것
//        한방 쿼리로 정보를 다 가져오는 것
//        하지만 이때 spring data jpa는 매일 Query를 적지 않기 때문에
//        jpql을 계속 써야하는 번거로움이 존재한다.
//        그래서 이걸 해결해주는 게 Entity Graph다.
//        메서드 이름으로 해결하는데 여기에 fetch조인까지 해주는 것

        em.clear();

        List<Member> all = memberRepository.findAll();
//        select m1_0.member_id,m1_0.age,t1_0.team_id,t1_0.name,m1_0.username
//        from member m1_0 left join team t1_0 on t1_0.team_id=m1_0.team_id;
//        이렇게 한방 쿼리로 전부 가져오는 것을 볼 수 있다.
//        내부적으로 fetch조인을 사용하는 것
        for (Member member : all) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());

        }
//        member.getUsername() = member1
//        member.getTeam().getClass() = class study.data_jpa.entity.Team
//        member.getTeam().getName() = teamA
//        member.getUsername() = member2
//        member.getTeam().getClass() = class study.data_jpa.entity.Team
//        member.getTeam().getName() = teamB

        em.clear();
        List<Member> entityGraphByUsername = memberRepository.findEntityGraphByUsername("member1");
        for (Member member : entityGraphByUsername) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
//        select m1_0.member_id,m1_0.age,t1_0.team_id,t1_0.name,m1_0.username
//        from member m1_0 left join team t1_0 on t1_0.team_id=m1_0.team_id
//        where m1_0.username='member1';
//        이렇게 member1에 대한 left join이 이뤄지는 것을 볼 수 있다.
//        entityGraph를 통해 fetch Join을 매우 편리하게 사용할 수 있다.
//        그런데 EntityGraph기능은 JPA에서 제공하는 기능으로 
//        JPA의 네임드 엔티티 그래프라는 기능도 존재


    }

    //JPA Hint & Lock
    //jpa표준으로 JPA 쿼리에 대한 힌트를 준다.
    //JPA구현체에게 제공하는 힌트
    //이게 SQL에 날리는 힌트가 아닌 JPA객체에게 제공되는 힌트다.
    @Test
    public void queryHint(){
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        //쿼리만 나가고 엔티티 영속성 컨텍스트 내부에 존재한다.
        em.clear();
        //이렇게 강제로 하면 영속성 컨텍스트가 비워진다.

        //when
//        Member findMember = memberRepository.findById(member1.getId()).get();
//        findMember.setUsername("member2");
//        em.flush();
        //update member set age=10,team_id=NULL,username='member2' where member_id=1;
        //이렇게 하면 더티체크로 인해 바뀐 데이터에 대한 업데이트 쿼리가 나간다.
        //이런 변경 감지를 하려면 치명적인 단점은 원본 데이터가 있어야 된다.
        //그래서 객체를 두개를 들고 있어야 하는 것이다.
        //원래 어떤 데이터였는 지 메모리에 보관하고 있는다.
        //그 후 더티 체크로 데이터를 체크한다.

        //데이터를 변경할 목적이 아닌 조회만 하는 목적일 경우
        //혹시나 더티체크를 위한 데이터를 가지고 있을 필요가 없기 떄문에
        //데이터를
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        //이렇게 가져올 경우 메모리에 기본 객체와 더티체크를 위한 객체
        //두개를 가지는데 이걸 최적화 하기 위해서
        //jpa표준은 지원하지 않지만 하이버네이트가 지원하는
        //힌트를 사용하는 것
        //이렇게 리포지토리 인터페이스에 힌트를 지정하면
        //readOnly최적화를 통해 스냅샷을 만들 지 않는다.
        //내부적으로 읽기로 인식하고 최적화를 해준다.

        //기술들이 좋아져서 성능테스트를 해보고 결정하면 되는데
        //이때 실무에서 조회 트래픽이 많을 경우
        //전체 중 리드온리 옵션을 넣어봐야 최적화가 잘 되는 것은
        //아니다. 성능이 정말 늦는 복잡한 조회 쿼리가 잘못됬을 때
        //문제가 생기는 것이지 이런 것은 보통 몇퍼센트 안된다.
        //그래서 굳이 이걸 다 넣을 필요가 있나? 없다. 정말 중요한 몇개만
        //넣는 게 좋다.
        //그리고 조회 성능이 부족하면 레디스 같은 캐시를 활용해서
        //최적화를 이미 했어야 된다.
        //처음부터 다 튜닝하는 것이 아니라
        //정말 성능에 문제가 있을 경우 단계적으로 하는 게 좋다.
        em.flush();




    }

    //Lock이란?
    @Test
    public void lock(){
        //given
        Member member1 = new Member("member1",10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        List<Member> findMember = memberRepository.findLockByUsername("member1");
//        select m1_0.member_id,m1_0.age,m1_0.team_id,m1_0.username
//        from member m1_0
//        where m1_0.username='member1' for update;
//        for update가 자동으로 붙는데
//        FOR UPDATE는 데이터베이스에서 동시성 제어를 위해 사용되는 SQL 구문입니다. 이 구문은 SELECT 쿼리에서 특정 데이터를 조회하는 동시에 해당 데이터를 다른 트랜잭션에서 수정하지 못하도록 락을 거는 역할을 합니다.
//
//        작동 방식:
//        FOR UPDATE를 사용하면 조회된 행(row)에 대해 **배타적 잠금(exclusive lock)**이 걸리게 됩니다.
//                즉, 해당 행에 대해 읽기는 가능하지만, 다른 트랜잭션이 수정하거나 삭제하는 작업은 막습니다. 이 트랜잭션이 끝나기 전까지(커밋 또는 롤백) 다른 트랜잭션이 이 행에 접근해 변경 작업을 시도할 수 없습니다.
//        주요 목적:
//        동시성 문제가 발생하지 않도록 방지.
//                데이터를 조회하면서 동시에 수정이 필요할 때, 다른 트랜잭션이 데이터를 수정하지 못하도록 잠금을 걸어 안정적인 데이터 수정 및 일관성을 보장.
//        예를 들어, 위의 쿼리에서 member1의 데이터를 가져올 때 FOR UPDATE가 붙으면 해당 데이터를 조회하는 동안 다른 트랜잭션은 이 데이터에 대해 변경 작업(예: UPDATE, DELETE)을 할 수 없습니다.
//
//        언제 유용한가?
//        다중 트랜잭션에서 동일한 데이터를 동시에 변경할 가능성이 있을 때, 데이터의 무결성을 보장하기 위해 사용됩니다.
//        이 기능은 방언에 따라 동작방식이 달라지는데
//        이때 Lock은 실시간 트래픽이 많을 경우에는 Lock을 걸면 안된다.
        //왜냐면 이 데이터를 손대는 것에 다 락이 걸려서
        //Optimastic Lock으로 하는 게 좋다.
        em.flush();
    }

    //기존의 리포지토리는 전부 인터페이스로 이루어져 있는데
    //이걸 구현하게 되면 인터페이스를 상속받아야 하니
    //전부 새롭게 구현해야 된다.
    //그래서 특정한 JDBC나 Mybatis리파지토리 기능을
    //사용하고 싶을 경우
    //이걸 사용할 수 있도록 열어놓은 것이
    //사용자 정의 리포지토리이다.
    //이러한 사용자 정의 리포지토리는 QueryDSL을 사용할 떄 많이 사용
    @Test
    public void callCustom(){
        List<Member> memberCustom = memberRepository.findMemberCustom();
    }
    //이렇게 실행하보면
    //select m1_0.member_id,m1_0.age,m1_0.team_id,m1_0.username from member m1_0;
    //이렇게 사용자 정의로 확장시킬 수 있다.
    //이때 이렇게 사용하는 건 대부분 QueryDSL을 사용할 때 커스텀을 많이 사용한다.
    //이때 규칙이 있는데 실제 Impl에서 지켜야 되는 것인데
    //MemberRepositoryImpl <<실제 구현체 이름을 이렇게
    //Repository이름과 Impl을 적어줘야 SDJ가 인식해서
    //내부의 구현체 함수를 불러올 수 있다.
    //만약 Impl을 사용할 수 없다면
    //@EnableJpaRepositories에서 RepositoryImplemeentationPostifx의
    //설정을 컨피그에서 변경하면 된다.
    //왠만하면 관례를 따르는 게 좋다.
    //유지보수에서 이런 관례가 중요하다.
    //인터페이스로 해결하기 힘들 경우에 사용하는데 
    //복잡한 동적 쿼리에 의해 자주 사용하게 될 것
    //핵심비즈니스 로직과 화면에 맞춘 쿼리를 분리해야 되는데
    //안되면 코드들이 너무 복잡해져서 유지보수가 힘들다.
    //핵심 비즈니스 리포지토리와 화면에 맞춘 쿼리를 리포지토리 자체를
    //쪼개서 사용하는 게 좋다.

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

    //JpaSpecificationExecutor테스트
    @Test
    public void specBasic(){
        //given
        Team teamA=new Team("TeamA");
        em.persist(teamA);
        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        //when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("TeamA"));
        //이렇게 excuter를 통해 실행된다.
        List<Member> result = memberRepository.findAll(spec);
        //specification을 생성해야 된다.
        Assertions.assertThat(result.size()).isEqualTo(1);
        //이너 조인을 통해 where를 통해 팀과 맴버 이름에 대해서 탐색하는 쿼리가 나가는 것을 볼 수 있다.
        //select m1_0.member_id,m1_0.age,m1_0.created_by,m1_0.created_date,m1_0.last_modified_by,m1_0.last_modified_date,m1_0.team_id,m1_0.username
        // from member m1_0 join team t1_0 on t1_0.team_id=m1_0.team_id where m1_0.username='m1' and t1_0.name='TeamA';
        //이렇게 자바코드로 편리하게 쿼리를 만들 수 있는데 문제는 이 구현하는 기술이 jpa 크라이테리아를 통해 jpql로 변경되어 나가지만
        //너무 복잡해서 코드를 이해하기 힘들다.
        //jpa가 제공하는 크라이테리아가 너무 복잡하기 때문에 QueryDSL을 사용하는 게 좋다.



    }

    //QueryByExample
    @Test
    public void queryByExample(){
        //given
        Team teamA=new Team("TeamA");
        em.persist(teamA);
        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        //when
        //m1을 조회하고 싶다면?
        memberRepository.findByUsername("m1");
        //위처럼 단일 조건 쿼리를 사용하면 되지만 조건이 여러개라면 이렇게 정적으로 사용하기 힘들다.
        //그래서 동적으로 변경되며 검색이 되도록 해야된다.
        //probe >>엔티티 자체가 검색 조건이 된다.
        Member member =new Member("m1");
        //복잡성 증가를 위한 Team추가
        Team team = new Team("TeamA");
        member.setTeam(team);
        //이렇게 연관관계로 검색조건 자체를 넣어버리면 된다.
        //이렇게 하면 유저 이름과 age는 무시하고 team의 값이 들어가니 팀의 name까지 매칭을 하게 된다.
        //select m1_0.member_id,m1_0.age,m1_0.created_by,m1_0.created_date,m1_0.last_modified_by,m1_0.last_modified_date,m1_0.team_id,m1_0.username
        // from member m1_0 join team t1_0 on t1_0.team_id=m1_0.team_id where m1_0.username='m1' and t1_0.name='TeamA';
        //이렇게 팀과 조인하여 데이터를 찾는 것을 볼 수 있다.
        //이렇게 객체 그래프틀 동해서 검색이 가능하지만 이너조인만 가능하고 아우터 조인같은 복잡한 조인에서는 잘 동작하지 않는다.
        //Probe는 ㅣㄹ드에 데이터가 있는 실제 도메인 객체
        //ExMatcher는 특정 필드를 일치시키는 상세한 정보 제공
        //Example은 probe와 Matcher로 구성하여 쿼리를 구성


        //age를 무시하기 위한 설정
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");
        //

//        Example<Member> example = Example.of(member);
        //이렇게 member엔티티로 검색하는 것

        //이후 age를 무시하기 위해서 별도로 matcher 파라미터를 넣어줘야 한다.
        Example<Member> example = Example.of(member,matcher);
        //이렇게 변경하면 자바 기본타입들은 0을 기본으로 할당되어 있기때문에 이렇게 무시하도록 해야된다.

        List<Member> result = memberRepository.findAll(example);
        //이렇게 example을 파라미터로 받는 것을 jpa 리포지토리 구현체가 기본값으로 되어 있다.

        assertThat(result.get(0).getUsername()).isEqualTo("m1");
        //이때 select를 할 때 age가 0인 이유는 도메인 객체를 가지고 검색 조건을 만드는 것인데
        //이때 username은 넣었지만  age는 자바의 null타입이 아니기 때문에 0이 되는것
        //그래서 age는 무시하도록 설정해야 한다.

        //하지만 이런 기술들에서 join에서 문제가 많이 일어난다.그래서 이때 join이 정상적으로 다 해결될 때만 실무에 도입하는 것이 좋다.
        //하지만 이건 join을 할때 inner만 가능하고 outter조인이 불가능하다.
        //그래서 복잡한 조인을 하게 되면 이걸 다시 걷어내야 한다.

        //이런 코드의 장점은 동적 쿼리를 편리하기 만들 수 있고
        //도메인 객체를 그대로 사용 가능
        //데이터 저장소를 RDB에서 NOSQL로 변경해도 코드 변경없이 추상화 가능
        //스프링 JPA리포지토리 자체에 이미 포함되어 있다.

        //하지만 외부조인이 불가능하며 중첩 제약 조건이 안되고  매칭 조건이 매우 단순하여 복잡한 조건에서는 사용하기 쉽지 않다.
        //그렇기 때문에 QueryDSL을 사용하자.


    }

    //Projections: 엔티티 대신 DTO를 편리하게 조회할 때 사용
    //쿼리의 Select절에 들어갈 데이터
    //
    @Test
    public void projections(){
        //given
        Team teamA=new Team("TeamA");
        em.persist(teamA);
        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        //when
        //이때 이름만 가져오고 싶을 경우
//        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1",UsernameOnlyDto.class);
        List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);
        //Projections
//        List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);
        //이렇게 유저 온리 인터페이스를 반환타입으로 넣으면 된다.
        //이렇게 하면 UsernameOnly내부에 프록시 객체로 데이터가 넘어온다.
//        for(UsernameOnlyDto usernameOnly:result){
//            System.out.println("usernameOnly = " + usernameOnly);
//        }
        //springData jpa에서 이렇게 인터페이스로 데이터를 받을 때 쿼리를 보면
        //member의 username만 요청해서
//        select m1_0.username from member m1_0 where m1_0.username='m1';
        //전체 엔티티가 아닌 내부 데이터만 가져오는 것을 확인할 수 있다.
        //이때 result에는 인터페이스를 가지고
        //usernameOnly = org.springframework.data.jpa.repository.query.AbstractJpaQuery$TupleConverter$TupleBackedMap@26b6454d
        //이렇게 인터페이스를 정의하면 스프링에서 프록시같은 기술을 통해 가짜 데이터를 만들어서 인터페이스의 구현체는
        //spring Data jpa가 만들어서 인터페이스로 데이터를 인식해서 필요한 데이터만 담아서 전달해준다.
        //데이터를 간편하게 가져올 수 있다.
        //이때 장점은 findBy같은 기존 구현체함수와 연동해서 사용할 수 있다.
        for (NestedClosedProjections nestedClosedProjections : result) {
            System.out.println("nestedClosedProjections = " + nestedClosedProjections);
            String username = nestedClosedProjections.getUsername();
            System.out.println("username = " + username);
            String TeamName = nestedClosedProjections.getTeam().getName();
            System.out.println("TeamName = " + TeamName);
        }
        //이렇게 확인해보면
        //username = m1
        //TeamName = TeamA
        //이렇게 데이터를 잘 가져오는데 이때 루트인 맴버에서는 최적화가 되지만 팀에서는 최적화가 안된다.
        //명확한 한계가 존재한다. 그래서 엔티티 하나를 넘어서는 순간(조인이 일어난 순간) 쓰기 애매하다.
        //복잡할때 DTO로 데이터를 가져오는거지/ 엔티티가 하나 있는데 이때는 엔티티를 조회해서 조회 후 DTO로 변환하는 게 더 편하다.
    }




}