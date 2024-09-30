package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.data_jpa.entity.Member;

import java.util.List;
import java.util.Optional;

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

    //회원 삭제
    public void delete(Member member){
        em.remove(member);
    }
    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }//jpql을 통해 list로 불러와야 한다.
    //Optional로 단일 조회
    public Optional<Member> findById(Long id){
        Member member=em.find(Member.class,id);
        return Optional.ofNullable(member);
    }
    //count쿼리
    public long count(){
        return em.createQuery("select count(m) from Member m",Long.class)
                .getSingleResult();
        //결과가 단건인 경우 singleResult사용

    }

    //회원의 이름과 나이로 검색을 하기 위해서는?
    public List<Member> findByUserNameAndAgeGreaterThan(String username,int age){
        return em.createQuery("select m from Member m where m.username=:username and m.age>:age")
                .setParameter("username",username)
                .setParameter("age",age)
                .getResultList();
    }
}
