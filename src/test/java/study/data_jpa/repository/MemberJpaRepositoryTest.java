package study.data_jpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

//import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//junit5에서는 RunWith를 안써도 된다.
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {
    @Autowired
    MemberJpaRepository memberJpaRepository;
    
    @Test
    public void testMember(){
        Member member=new Member("memberA");
        //생성자로 파라미터를 넘기는 게 더 좋은 방식
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        //test의 Transactional은 rollback이 기본으로 존재
        //만약 직접 확인하고 싶으면 @Rollback(value = false)로 설정
        assertThat(findMember).isEqualTo(member);
        //같은 영속성 컨텍스트를 조회하기 때문에 같다.
    }

    @Test
    public void bassicCRUD(){
        Member member1=new Member("member1");
        Member member2=new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        
        //단건 조회 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
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

//        //리스트 조회 검증
//        List<Member> all = memberJpaRepository.findAll();
//        assertThat(all.size()).isEqualTo(2);
//
//        //카운트 검증
//        long count = memberJpaRepository.count();
//        assertThat(count).isEqualTo(2);
//
//        //삭제 검증
//        memberJpaRepository.delete(member1);
//        memberJpaRepository.delete(member2);
//
//        long deletedCount = memberJpaRepository.count();
//        assertThat(deletedCount).isEqualTo(0);

    }
    //findByUserNameAndAgeGreaterThan 검증
    @Test
    public void findByUserNameAndAgeGreaterThan(){
        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("aaa", 20);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        List<Member> result = memberJpaRepository.findByUserNameAndAgeGreaterThan("aaa", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("aaa");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery(){
        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("bbb", 20);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        List<Member> result = memberJpaRepository.findByUsername("aaa");
        Member findMember=result.get(0);
        assertThat(findMember).isEqualTo(member1);
        //테스트가 정상적으로 성공한 것을 볼 수 있다. 하지만 이걸 구현하는게 상당히 번거롭다.

    }
}