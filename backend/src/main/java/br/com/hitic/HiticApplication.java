package br.com.hitic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("br.com.hitic.repository")
public class HiticApplication {

	public static void main(String[] args) {
		SpringApplication.run(HiticApplication.class, args);
	}

}
