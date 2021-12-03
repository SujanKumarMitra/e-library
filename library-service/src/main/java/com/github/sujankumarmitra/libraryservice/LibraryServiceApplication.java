package com.github.sujankumarmitra.libraryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.tools.agent.ReactorDebugAgent;

//@SpringBootApplication(exclude = {R2dbcAutoConfiguration.class, R2dbcDataAutoConfiguration.class})
@SpringBootApplication
public class LibraryServiceApplication {

//	@Bean
//	public DatabaseClient client() {
//		return new NoOpDatabaseClient();
//	}

	public static void main(String[] args) {
		ReactorDebugAgent.init();
		SpringApplication.run(LibraryServiceApplication.class, args);
	}

}
