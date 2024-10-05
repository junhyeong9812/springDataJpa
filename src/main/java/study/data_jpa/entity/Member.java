package study.data_jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of={"id","username","age"})
//여기에 team같은 연관관계를 적으면 이걸 타서 순환구조가 나오기 때문에
//무한루프에 빠져서 에러가 난다.
@NamedQuery(
        name="Member.findByUsername",
        query="select m from Member m where m.username= :username"
)//이렇게 엔티티에 Named쿼리를 통해 생성할 쿼리를 볼 수 있다.
//JPA리포지토리에서도 이 @NamedQuery는 존재해야 된다.
@NamedEntityGraph(name = "Member.all",
        attributeNodes = @NamedAttributeNode("team"))
//
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }
    //    protected Member() {
//    }//protected를 통해 생성자를 통해 만드는 것을 막고
    //jpa의 프록시에서 접근이 가능하도록 private가 아닌 protected로 해놔야
    //구현체가 객체를 강제로 만들 때 사용할 수 있다.

    //
    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team team) {
        this.username=username;
        this.age=age;
        if(team!=null) {
            changeTeam(team);
        }//null일 경우에는 무시하도록 설정
    }

    public void changeUserName(String username){
        this.username=username;
    }

    //연관관계 편의 메소드
    public void changeTeam(Team team){
        this.team=team;
        team.getMembers().add(this);
        //team의 Members에도 이 맴버 객체를 추가해줘야 한다.
    }
}
