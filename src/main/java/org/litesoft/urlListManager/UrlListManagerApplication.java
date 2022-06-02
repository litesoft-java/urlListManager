package org.litesoft.urlListManager;

import org.litesoft.urlListManager.persistence.FileUrlRepositoryFactory;
import org.litesoft.urlListManager.persistence.UrlRepositoryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@SuppressWarnings("resource")
public class UrlListManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlListManagerApplication.class, args);
	}

	@Bean
	UrlRepositoryFactory repoFactory() {
		return new FileUrlRepositoryFactory( System.getProperty("user.dir") );
	}
}
