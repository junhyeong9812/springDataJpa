package study.data_jpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.data_jpa.entity.Member;
import study.data_jpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {
    //웹 확장 도메인 클래스 컨버터
    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }
    //이때 위 id는 pk로 들어가서 이때는 도메인 클래스 컨버터를 사용할 수 있다.
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){
        //이처럼 id를 통해 user를 찾아서 넣는게 아닌
        //Spring이 컨버팅 과정을 끝내고 결과를 인젝션 해준다.
        //SpringDataJpa가 기본으로 해준다.
        //받는 pk를 패스로 받아서 바로 조회한 것
        //이게 도메인 클래스 컨버터라 한다.
        //이런 기능은 유지보수차원에서 직관적이지 않아서
        //PK로 막 조회 하는 경우가 거의 없고
        //PK를 공개하는 경우는 드믈다.
        return member.getUsername();
        //하지만 위처럼 파라미터를 받을 경우 조회로 사용해야 된다.
        //왜냐면 트랜젹션으로 조회한게 아니라 영속성 생명주기가 애매해질 수 있다.

    }

    //앱 실행 시 userA가 생성
    @PostConstruct
    public void init(){
        memberRepository.save(new Member("userA"));
    }


}
