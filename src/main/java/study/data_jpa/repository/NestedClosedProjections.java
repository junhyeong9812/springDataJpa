package study.data_jpa.repository;
//유저이름과 팀의 이름을 가져오는 프로젝션 타입
public interface NestedClosedProjections {
    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo{
        String getName();
    }

}
//select m1_0.username,t1_0.team_id,t1_0.created_by,t1_0.created_date,t1_0.last_modified_by,t1_0.last_modified_date,t1_0.name
// from member m1_0 left join team t1_0 on t1_0.team_id=m1_0.team_id where m1_0.username='m1';
//이렇게 쿼리를 보면 맴버는 이름만 가져오지만 팀은 다 가져오는 것을 볼 수 있다.
//첫번째 루트는 최적화가 되지만 두번째부터는 최적화가 아닌 모든 데이터를 다 가져온다.조인은 left outer 조인까지 한다.
//레프트 조인이라 데이터를 가져오는 부분에서는 안정성이 보장되는 코드이다.
