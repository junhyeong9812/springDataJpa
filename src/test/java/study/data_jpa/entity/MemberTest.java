package study.data_jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.repository.MemberRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity(){
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("Member1",10,teamA);
        Member member2 = new Member("Member2",20,teamA);
        Member member3 = new Member("Member3",30,teamB);
        Member member4 = new Member("Member4",40,teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        //초기화
        em.flush();
        em.clear();
        //영속성 컨텍스트 캐시를 날리고 테스트를 하기 위해서
        //flush와 clear까지
        
        //확인
        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
        for(Member member:members){
            System.out.println("member = " + member);
            System.out.println("member.getTeam() = " + member.getTeam());
        }
    }

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
    @Test
    public void JpaEventBaseEntity() throws Exception{
        //given
        Member member = new Member("member1");
        memberRepository.save(member);//@PrePersist
        
        Thread.sleep(100);
        member.setUsername("member2");
        em.flush();//@PreUpdate
        em.clear();
        //when
        Member findMember = memberRepository.findById(member.getId()).get();

        //then
        System.out.println("findMember.getCreatedDate() = " + findMember.getCreatedDate());
//        System.out.println("findMember.getUpdatedDate() = " + findMember.getUpdatedDate());
        System.out.println("findMember.getUpdatedDate() = " + findMember.getLastModifiedDate());
        System.out.println("findMember.getCreatedBy() = " + findMember.getCreatedBy());
        System.out.println("findMember.getLastModifiedBy() = " + findMember.getLastModifiedBy());

    }
//    findMember.getCreatedDate() = 2024-10-10T16:59:12.959486
//    findMember.getUpdatedDate() = 2024-10-10T16:59:13.072755
//    이렇게 정상적으로 이벤트가 발생한 것을 확인할 수 있으며
//    테이블에도 정상적으로 생성 시간 및 업데이트 시간에 대해 타임스템프로 들어가있는 것을
//    알 수 있다.
//    이렇게 베이스엔티티를 가지고 있으면 상속을 통해 날짜 및 시간 데이터를 넣어야될 경우
//    그 엔티티에서 상속하여 사용하면 된다.
    
    //하지만 Spring data Jpa에서는 이러한 문제를 더 쉽게 해결할 수 있게
    //해주는데
    //@EnableJpaAuditing설정이 필요 App에서 설정

//    findMember.getCreatedDate() = 2024-10-10T17:13:56.105151
//            findMember.getUpdatedDate() = 2024-10-10T17:13:56.224940
//            findMember.getCreatedBy() = 3cedc0f7-7efb-4e13-a410-84bfca8b5f99
//findMember.getLastModifiedBy() = e9930fa1-6dcb-46b2-94bb-0a4338f73e73
//  지금은 UUID를 랜덤으로 돌려서 저렇게 나오지만
//    이때 나중에 실제 사용할 경우 세션등에서 사용자 정보를 가져와서 넣으면 되낟.

}