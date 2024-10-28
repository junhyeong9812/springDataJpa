package study.data_jpa.repository;


//Projections: 엔티티 대신 DTO를 편리하게 조회할 때 사용
//쿼리의 Select절에 들어갈 데이터
//

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    @Value("#{traget.username+''+target.age}")
    String getUsername();
    //이렇게 인터페이스로 생성 후
}
//이렇게 인터페이스 기반으로 할 수있는 것과
//인터페이스 기반에서 오픈 프로젝션을 사용하는 방법도 존재하는데
//위처럼 value로 @Value("#{traget.username+''+target.age")
//이렇게 사용자 이름과 나이를 둘다 가져와서 이걸 하나의 문자열에 담아준다.
//이렇게 하면 오픈프로젝션이라 하여
//사용하면 맴버의 모든 엔티티를 가져와서 거기서 value데이터를 계산해서 문자열로 만들어서 반환해주는 것
//usernameOnly = Member(id=1, username=m1, age=0)
//결국 모든 데이터를 가져와서 원하는 데이터만 가져와서 문자열로 처리하는 것이다.
//엔티티를 다 가져와서 처리하는 것이 오픈 프로젝션이며
//클로즈 프로젝션은 필요한 데이터만 가져와서 정렬하는 것이다.
