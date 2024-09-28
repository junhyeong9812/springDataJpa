package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.data_jpa.entity.Team;

import java.util.List;
import java.util.Optional;

@Repository
public class TeamRepository {
    @PersistenceContext
    private EntityManager em;

    //Team저장
    public Team save(Team team){
        em.persist(team);
        return team;
    }

    //Team 삭제
    public void delete(Team team){
        em.remove(team);
    }
    //Team 전체 조회
    public List<Team> findAll(){
        return em.createQuery("select t from team t", Team.class)
                .getResultList();
    }
    //Team 단일 조회
    public Optional<Team> findById(Long id){
        Team team=em.find(Team.class,id);
        return Optional.ofNullable(team);
        //shift+f6으로 변수명 변경가능
    }

    //count조회
    public long count(){
        return em.createQuery("select count(t) from Team t",Long.class)
                .getSingleResult();
    }

    //Member와 Team을 보면 기본적인 CRUD는 코드 형식이 비슷한 것을 확인할 수 있다.
    //또한 이때 update는 더티체크를 통해 변경할 수 있기 때문에 그걸 활용한다.
    //
}
