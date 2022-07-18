package org.litesoft.urlListManager;

import org.litesoft.urlListManager.persistence.FileUrlRepositoryFactory;
import org.litesoft.urlListManager.persistence.UrlRepositoryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@SuppressWarnings("resource")
public class UrlListManagerApplication {
	public static final String VERSION = "0.3";

	public static void main(String[] args) {
		System.out.println(ATTENTION + "URL List Manager vs " + VERSION + ATTENTION);
		SpringApplication.run(UrlListManagerApplication.class, args);
	}

	@Bean
	UrlRepositoryFactory repoFactory() {
		return new FileUrlRepositoryFactory( USER_DIR );
	}

	private static final String USER_DIR = System.getProperty( "user.dir" );
	private static final String ATTENTION = " ******************************************** ";
}
