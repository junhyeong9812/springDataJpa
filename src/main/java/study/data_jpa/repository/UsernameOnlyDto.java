package study.data_jpa.repository;

public class UsernameOnlyDto {
    //클래스 기반 프로젝션
    private final String username;

    public UsernameOnlyDto(String username) {
        //클래스 기반 프로젝션은
        //생성자의 파라미터 이름으로 매칭을 시켜서 프로젝션을 하는 것이다.
        //이 생성자의 파라미터 명은 일치해야 된다.
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
//select m1_0.username from member m1_0 where m1_0.username='m1';
//이렇게 UsernameOnlyDto로 데이터가 들어가 있는 것을 확인할 수 있고 단일 데이터를 보는 것도 알 수 있다.
