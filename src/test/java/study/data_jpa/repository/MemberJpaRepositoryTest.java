package study.data_jpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.entity.Member;

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

}