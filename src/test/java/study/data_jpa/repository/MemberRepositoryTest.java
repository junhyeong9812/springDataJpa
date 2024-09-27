package study.data_jpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.entity.Member;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
//    @Test
//    public void testMember(){
//        Member member= new Member("memberA");
//        memberRepository.save(member);
//        //Save라는 함수가 인터페이스에 상속만 했는데 기능이 제공이 되는 것을 확인할 수 있다.
//        //JpaRepository도 인터페이스인데 save기능이 제공되고
//        //findById 기능도 제공이 되는데 이렇게 인터페이스만 있으면 기본적인
//        //코드들은 JPA리포지토리가 만들어서 제공해준다.
//        //기존의JPA코드와 거의 같지만 직접 코드를 작성하지 않아도 된다는 장점이 존재한다.
//
//        Member savedMember =memberRepository.save(member);
//        Member findMember = memberRepository.findById(savedMember.getId()).get();
//        //데이터의 존재 유무를 모르기 때문에 Optional로 제공하기에 .get을 통해
//        //데이터를 꺼내야 한다. 하지만 null이면 NosuchElent익셉션이 터진다.
//        assertThat(findMember.getId()).isEqualTo(member.getId());
//        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
//        assertThat(findMember).isEqualTo(member);
//
//
//    }
}