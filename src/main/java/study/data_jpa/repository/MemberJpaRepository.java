package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.data_jpa.entity.Member;

@Repository
public class MemberJpaRepository {
    @PersistenceContext//Spring-boot가 Jpa의 영속성 컨텍스트의 EntityManager를 넣어준다.
    private EntityManager em;
    //회원저장
    public Member save(Member member){
        em.persist(member);
        return member;
    }
    //회원 단일 조회
    public Member find(Long id){
        return em.find(Member.class,id);
    }

}
