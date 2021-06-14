package io.github.appuhafeez.tiktoktoe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@EnableJpaRepositories
@CrossOrigin(origins = "${allowed.origins}",allowedHeaders = "*")
public class TictoctoeAuthServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TictoctoeAuthServerApplication.class, args);
	}

}
