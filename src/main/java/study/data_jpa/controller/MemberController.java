package study.data_jpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.data_jpa.dto.MemberDto;
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
//    @PostConstruct
//    public void init(){
//        memberRepository.save(new Member("userA"));
//    }

    //springData는 페이징과 정렬을 MVC에서 더 편리하게 사용할 수 있도록
    //기능을 제공한다.
    //

    //web확장 -페이징과 정렬
//    @GetMapping("/members")
//    public Page<Member> List(Pageable pageable){
//        //Pageable인터페이스 필요
//        //Page는 결과정보 인터페이스
//        //바로 바인딩을 해준다.
//        Page<Member> all = memberRepository.findAll(pageable);
//        //pageAndSortRepository의 Sort로 정렬을 해준다.
//        //기본 메소드에 Pageable파라미터를 넘기면 된다.
//        return all;
//        //이렇게 반환을 할 수 있다.
//
//    }
//    @GetMapping("/members")
//    public Page<Member> List(@PageableDefault(size=6,sort="username") Pageable pageable){
//        //이렇게 전역이 아닌 특별하게 이 요청에만 적용하고 싶을 경우
//        //이처럼 페이저블디폴트를 통해 설정하면 된다.
//        Page<Member> all = memberRepository.findAll(pageable);
//        //pageAndSortRepository의 Sort로 정렬을 해준다.
//        //기본 메소드에 Pageable파라미터를 넘기면 된다.
//        return all;
//        //이렇게 반환을 할 수 있다.
//
//    }
    //또한 페이징 하는 객체가 두개이상이면 접두사를 통해 구분할 수 있다.
    //또한 Page의 내용을 DTO로 변환할 때는
    //엔티티의 노출을 하지 않기 위해서 하는 것으로 내부 설계를 감추기 위함도 존재한다.
    //그리고 엔티티를 노출하게 되면 API을 가져가 사용할 떄
    //엔티티의 변경 사항마다 API스팩이 변경되어 API를 사용하는 쪽에서도
    //지속적으로 스팩을 바꿔야 된다.
    //그렇기 때문에 DTO를 통해 반환해줘야 한다.
    @GetMapping("/members")
    public Page<MemberDto> List(@PageableDefault(size=6,sort="username") Pageable pageable){
        //직접 Page를 1부터 시작하도록 하려면
        PageRequest request = PageRequest.of(1,2);
        Page<Member> page1 = memberRepository.findAll(request);
        //이렇게 넘겨서 page에 대해서 Page로 감싸는게 아닌
        //별개의 page객체를 만들어서 사용해야 된다.
        //API규격에 맞도록 설정

        Page<Member> all = memberRepository.findAll(pageable);
//        Page<MemberDto> map = all.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        //이렇게 map함수를 통해 Dto로 변환하면 된다.
        Page<MemberDto> map = all.map(MemberDto::new);
        //이렇게 되면 메서드 레퍼런스로 변경 가능하다.
        //훨씬 간편하게 코드를 조정할 수 있다.
        return map;
        //ctrl alt n을 통해서 인라인 반환식으로 변경할 수도 있다.
        //또한 Dto는 엔티티를 받아도 된다.
        //엔티티는 Dto를 보지 않는게 좋지만
        //Dto들은 엔티티에 의존하는 방식으로 해도 된다.

        //또한 이렇게 반환하게 되면 이미 last/totalPages/size
        //등등 데이터들이 다 나온다.
        //이걸 가지고 프론트쪽에게 데이터를 넘겨주면 훨씬 간편하게 사용할 수 있다.
        //Page를 1부터 시작하고 싶다면
        //1.직접 클래스를 만들어서 처리한다.
        //PageRequest.of(1,2);
        //2.Spring.data.web.pageble.one-indexed-parameters를
        //true로 설정
        //이 파라미터의 한계는 page파라미터를 처리할 때 -1만 해서처리하는 것
        //하지만 이렇게 실행하면 페이지가 0일때는 같지만
        //페이지가 1일 경우에도 -1이 되서 0페이지가 나오고
        //이런 방식으로 동작하는 것
        //이 방식은 명확한 한계가 존재하는 데
        //데이터를 요청할 경우 send 부분에 Pageable객체 안에
        //데이터들과 서로 매칭이 안된다.
        //Page내부의 인덱스 데이터들은 page=0일때 가정하여
        //매칭이 안된다.
        //pageNumber와 number가 서로 맞지 않는다.
        //그래서 만약 사용할 경우 별도 커스텀을 통해 본인에게 맞는
        //형식으로 구동시키는 게 좋다.

    }


    //web확장 페이징
    //테스트 초기값
    @PostConstruct
    public void init(){
        for(int i=0;i<100;i++){
            memberRepository.save(new Member("user"+i,i));
        }
    }//스프링이 동작할때 테스트 데이터가 자동으로 들어간다.
    //members로 요청해보면 1부터 100까지의 데이터가 json으로 넘어온다.
}   //http://localhost:8080/members?page=0
//  또한 이렇게 ?page=0으로 하면 1부터 20까지 데이터가 들어오고
//page=1일 경우 21부터 40까지의 데이터가 들어온다.
//http://localhost:8080/members?page=2&size=3
//이렇게 size를 하면 그 크기만큼의 데이터만 가져온다.
//이게 pageable의 구현체에 들어가는 springboot가 세팅해주는 것이다.
//http://localhost:8080/members?page=2&size=3&sort=id,desc
//이렇게 하면 id로 정렬 후 역순으로 데이터를 가져오도록 할 수도 있다.
//이렇게 pageable객체를 통해 훨씬 편리하게 데이터들을 요청할 수 있다.
//sort는 ASC가 기본이기에 생략 가능
//만약 기본값을 변경하고 싶으면 yml파일에서 설정을 변경할 수 있다.
//jpa하위에
//data:
//web:
//pageable:
//default-page-size: 10
//max-page-size: 2000
//이렇게 기본값을 설정할 수 있따.
