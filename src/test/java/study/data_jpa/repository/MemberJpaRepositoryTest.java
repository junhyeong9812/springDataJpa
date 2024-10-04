package study.data_jpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

//import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//junit5에서는 RunWith를 안써도 된다.
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {
    @Autowired
    MemberJpaRepository memberJpaRepository;
    
    @Test
    public void testMember(){
        Member member=new Member("memberA");
        //생성자로 파라미터를 넘기는 게 더 좋은 방식
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        //test의 Transactional은 rollback이 기본으로 존재
        //만약 직접 확인하고 싶으면 @Rollback(value = false)로 설정
        assertThat(findMember).isEqualTo(member);
        //같은 영속성 컨텍스트를 조회하기 때문에 같다.
    }

    @Test
    public void bassicCRUD(){
        Member member1=new Member("member1");
        Member member2=new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        
        //단건 조회 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //유저 업데이트 확인
        findMember1.setUsername("member!!!!!!");
        //DB 쿼리 결과
//        AGE  	MEMBER_ID  	TEAM_ID  	USERNAME
//        0	    1	        null	member!!!!!!
//        0	    2	        null	member2
//        이렇게 이름이 변경된 것을 확인할 수 있다.
//        2024-09-28T21:38:23.244+09:00  INFO 17396 --- [data-jpa] [           main] p6spy                                    : #1727527103244 | took 0ms | statement | connection 3| url jdbc:h2:tcp://localhost/~/datajpa
//        update member set age=?,team_id=?,username=? where member_id=?
//        update member set age=0,team_id=NULL,username='member!!!!!!' where member_id=1;
//        이렇게 쿼리로 update문이 나가는 것을 알 수 있다.

//        //리스트 조회 검증
//        List<Member> all = memberJpaRepository.findAll();
//        assertThat(all.size()).isEqualTo(2);
//
//        //카운트 검증
//        long count = memberJpaRepository.count();
//        assertThat(count).isEqualTo(2);
//
//        //삭제 검증
//        memberJpaRepository.delete(member1);
//        memberJpaRepository.delete(member2);
//
//        long deletedCount = memberJpaRepository.count();
//        assertThat(deletedCount).isEqualTo(0);

    }
    //findByUserNameAndAgeGreaterThan 검증
    @Test
    public void findByUserNameAndAgeGreaterThan(){
        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("aaa", 20);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        List<Member> result = memberJpaRepository.findByUserNameAndAgeGreaterThan("aaa", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("aaa");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery(){
        Member member1 = new Member("aaa", 10);
        Member member2 = new Member("bbb", 20);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        List<Member> result = memberJpaRepository.findByUsername("aaa");
        Member findMember=result.get(0);
        assertThat(findMember).isEqualTo(member1);
        //테스트가 정상적으로 성공한 것을 볼 수 있다. 하지만 이걸 구현하는게 상당히 번거롭다.

    }

    //순수 Jpa 페이징과 정렬
    @Test
    public void paging(){
        //given
        memberJpaRepository.save(new Member("member1",10));
        memberJpaRepository.save(new Member("member2",10));
        memberJpaRepository.save(new Member("member3",10));
        memberJpaRepository.save(new Member("member4",10));
        memberJpaRepository.save(new Member("member5",10));

        //page =1 offset =0 limit 10 ,page =2 offset=11 limit=20
        int age =10;
        int offset=0;
        int limit =3;
        //페이징 계산은 직접
        //when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
//        select m1_0.member_id,m1_0.age,m1_0.team_id,m1_0.username
//        from member m1_0 where m1_0.age=10 order by m1_0.username
//        desc offset 0 rows fetch first 3 rows only;

        long totalCount = memberJpaRepository.totalCount(age);
//        select count(m1_0.member_id) from member m1_0 where m1_0.age=10;

//        위처럼 쿼리가 나가는 것을 알 수 있다.

        //sql로 개발할 때는 페이징을 하는 코드들을 찾아서 사용해도 된다.
        //페이지 계산 공식을 적용
        //totalPage=totalCount/size...
        //마지막 페이지
        //최초 페이지
        // 페이징 계산
        int totalPages = (int) Math.ceil((double) totalCount / limit); // 총 페이지 수 계산
        int currentPage = offset / limit + 1; // 현재 페이지
        boolean isFirstPage = currentPage == 1; // 첫 페이지 여부
        boolean isLastPage = currentPage == totalPages; // 마지막 페이지 여부

        //then
        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }

    //벌크 수정 쿼리 테스트
    @Test
    public void bulkUpdate(){
        //given
        memberJpaRepository.save(new Member("member1",10));
        memberJpaRepository.save(new Member("member2",19));
        memberJpaRepository.save(new Member("member3",20));
        memberJpaRepository.save(new Member("member4",21));
        memberJpaRepository.save(new Member("member5",40));

        //when
        int resultCount = memberJpaRepository.bulkAgePlus(20);

        //then
        assertThat(resultCount).isEqualTo(3);
//        update member m1_0 set age=(m1_0.age+1) where m1_0.age>=20;
        //실제 DB로 보면 20보다 높은 모든 값이 1씩 더해져있는 것을 알 수 있다.
    }
}