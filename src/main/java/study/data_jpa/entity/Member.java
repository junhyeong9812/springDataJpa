package study.data_jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    private Long id;
    private String username;

    protected Member() {
    }//protected를 통해 생성자를 통해 만드는 것을 막고
    //jpa의 프록시에서 접근이 가능하도록 private가 아닌 protected로 해놔야
    //구현체가 객체를 강제로 만들 때 사용할 수 있다.

    public Member(String username) {
        this.username = username;
    }

    public void changeUserName(String username){
        this.username=username;
    }
}
