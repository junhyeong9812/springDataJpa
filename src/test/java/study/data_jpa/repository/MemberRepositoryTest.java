package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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
        Member findMember = memberRepository.findById(member1.getId()).get();
        findMember.setUsername("member2");
        em.flush();
        //이렇게 하면 더티체크로 인해 바뀐 데이터에 대한 업데이트 쿼리가 나간다.
    }

}