package study.data_jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id","name"})
public class Team extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team")
    //fk가 없는 곳에 mappedBy설정
    private List<Member> members=new ArrayList<>();
    //팀 생성자
    public Team(String name) {
        this.name = name;
    }
}
