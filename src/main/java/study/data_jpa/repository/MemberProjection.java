package study.data_jpa.repository;

public interface MemberProjection {
    Long getId();
    String getUsername();
    String getTeamName();
}
//프로젝션 네이티브를 위한 DTo형식 지정
