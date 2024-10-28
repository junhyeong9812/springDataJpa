package study.data_jpa.repository;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

public class MemberSpec {
    public static Specification<Member> teamName(final String teamName){
//        return new Specification<Member>(){
//            @Override
//            public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
//                //root는 처음 엔티티를 쿼리와 빌더를 통해 생성하는 것
//                if(StringUtils.isEmpty(teamName)){
//                    return null;
//                }
//
//                Join<Member, Team> t = root.join("team", JoinType.INNER);//회원과 조인
//                return builder.equal(t.get("name"),teamName);
//                //팀과 맴버를 조인해서 where문을 생성할 때 빌더를 통해 생성하여 추적하는 것
//
//            }
//        };
    //위 코드를 람다로 변경하면 이렇게 변경되는 것
        return (root, query, builder) -> {
            //root는 처음 엔티티를 쿼리와 빌더를 통해 생성하는 것
            if(StringUtils.isEmpty(teamName)){
                return null;
            }

            Join<Member, Team> t = root.join("team", JoinType.INNER);//회원과 조인
            return builder.equal(t.get("name"),teamName);
            //팀과 맴버를 조인해서 where문을 생성할 때 빌더를 통해 생성하여 추적하는 것

        };
    }

    public static Specification<Member> username(final String username){
        return (root, query, builder) -> builder.equal(root.get("username"),username);

    }
}
