package study.data_jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@EnableJpaRepositories(basePackages = "study.data_jpa.repository")
//부트는 자동으로 컴포넌트 스캔을 통해 리포지토리를 찾아주기 때문에 없어도 된다.
//만약 리포지토리가 외부에 있다면 필요
@SpringBootApplication
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

}
