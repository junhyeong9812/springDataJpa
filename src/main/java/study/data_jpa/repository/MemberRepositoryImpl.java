package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.data_jpa.entity.Member;

import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{
    //순수한 JPA로 사용하고 싶다면

    private final EntityManager em;


    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
    //이렇게 직접 구현하여 사용하는 것
    //이렇게 하고 MemberRepository가 알 수 있도록
    //extend로 MemberRepositoryCustom인터페이스를 상속
    //상속을 하면 실제 실행하면 이 메소드가 실행하는것
    //이건 spring data jpa가 엮어서 실행하도록 해준다.

}
