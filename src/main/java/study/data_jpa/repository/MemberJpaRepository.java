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

    public List<Member> findByUsername(String username){
        return em.createNamedQuery("Member.findByUsername",Member.class)
                .setParameter("username",username)
                .getResultList();
    }

    //Jpa 페이징과 정렬
    //SQL로 페이징과 정렬을 하려면 엄청 힘든데
    //row num을 여러번 사용해야 되는 복잡함이 존재한다.
    //검색 조건 나이 10/이름으로 내림차순/페이징 조건: 첫번째 페이지, 페이지당
    //보여줄 데이터는 3건
    public List<Member> findByPage(int age, int offset,int limit){
        return em.createQuery("select m from Member m " +
                "where m.age = :age order by m.username desc")
                .setParameter("age",age)
                .setFirstResult(offset)//시작부분
                .setMaxResults(limit)//끝부분
                .getResultList();
        //이렇게 해서 페이징을 해서 데이터를 가져오는데 이때
        //토탈 카운트를 통해 몇번째 페이지인 지 하기 위해서
    }
    //토탈 카운트
    public long totalCount(int age){
        return em.createQuery("select count(m) from Member m where " +
                "m.age = :age", Long.class)
                .setParameter("age",age)
                .getSingleResult();
    }
    //총 카운트와 페이징을 위한 오프셋을 볼 수 있다.
    //그리고 총 카운트에서는 소팅(정렬)이 필요 없기 때문에 카운트에서는 안씀

    //또한 이때 jpa는 방언으로 동작하기 때문에 DB가 바뀌여도 상관이 없다.

}
