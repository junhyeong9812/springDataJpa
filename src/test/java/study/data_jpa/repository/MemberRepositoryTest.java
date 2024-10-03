package study.data_jpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    }


}