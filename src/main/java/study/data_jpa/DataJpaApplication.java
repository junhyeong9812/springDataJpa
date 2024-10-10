package study.data_jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

//@EnableJpaRepositories(basePackages = "study.data_jpa.repository")
//부트는 자동으로 컴포넌트 스캔을 통해 리포지토리를 찾아주기 때문에 없어도 된다.
//만약 리포지토리가 외부에 있다면 필요
@EnableJpaAuditing
//이걸 통해  auditing을 더 편리하게 사용 가능
//@EnableJpaAuditing(modifyOnCreate = false)
////이렇게 false로 하면 update는 null로 들어가지만
//그냥 업데이트할 때도 기본 사용자나 생성 시간을 넣어주는 게 훨씬 좋다. null보다
@SpringBootApplication
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	private static Optional<String> getCurrentAuditor() {
		return Optional.of(UUID.randomUUID().toString());
	}//이렇게 구현하는 것
	//new AuditorAware<String>이 인터페이스에서 메서드 한개면
	//람다로 변경해서 위처럼 람다식을 사용하는데
	//실제로는 스프링 시큐리티를 통해 세션정보를 가져와서
	//그 id를 가져와서 꺼내서 넣어줘야 한다.
	//유저정보를 세션에서 가져와서 넣어주는 것
	//CreateBy/LastModifiedBy는 이렇게
	//Bean을 통해 AuditorProvider를 호출해서
	//결과물을 꺼내서 자동으로 넣도록 하는 것

	@Bean
	public AuditorAware<String> auditorProvider(){
		return DataJpaApplication::getCurrentAuditor;
	}
}
