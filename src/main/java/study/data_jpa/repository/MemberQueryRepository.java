package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.data_jpa.entity.Member;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final EntityManager em;

    List<Member> findAllMembers(){
        return em.createQuery("select m from Member m")
                .getResultList();
    }
    //이렇게 별도의 화면에 맞춘 쿼리를 이렇게 만드는 방식이다.
}
