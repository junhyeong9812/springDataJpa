package study.data_jpa.repository;

import study.data_jpa.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    //기존의 리포지토리는 전부 인터페이스로 이루어져 있는데
    //이걸 구현하게 되면 인터페이스를 상속받아야 하니
    //전부 새롭게 구현해야 된다.
    //그래서 특정한 JDBC나 Mybatis리파지토리 기능을
    //사용하고 싶을 경우
    //이걸 사용할 수 있도록 열어놓은 것이
    //사용자 정의 리포지토리이다.
    //이러한 사용자 정의 리포지토리는 QueryDSL을 사용할 떄 많이 사용
    //이렇게 기본 인터페이스를 만들고
    //이후 구현 클래스를 별도로 만든다.

    List<Member> findMemberCustom();
}
