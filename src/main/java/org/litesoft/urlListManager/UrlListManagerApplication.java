package org.litesoft.urlListManager;

import org.litesoft.urlListManager.persistence.FileUrlRepositoryFactory;
import org.litesoft.urlListManager.persistence.UrlRepositoryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@SuppressWarnings("resource")
public class UrlListManagerApplication {
	public static final String VERSION = "0.2";

	public static void main(String[] args) {
		SpringApplication.run(UrlListManagerApplication.class, args);
	}

	@Bean
	UrlRepositoryFactory repoFactory() {
		String userDir = System.getProperty( "user.dir" );
		System.out.println(ATTENTION + "URL List Manager vs " + VERSION + ATTENTION);
		return new FileUrlRepositoryFactory( userDir );
	}

	private static final String ATTENTION = " ******************************************** ";
}
