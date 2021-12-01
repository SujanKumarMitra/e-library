package com.github.sujankumarmitra.libraryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication(exclude = {R2dbcAutoConfiguration.class, R2dbcDataAutoConfiguration.class})
//@SpringBootApplication
public class LibraryServiceApplication {

	@Bean
	public DatabaseClient client() {
		return new NoOpDatabaseClient();
	}

	public static void main(String[] args) {
		ReactorDebugAgent.init();
		SpringApplication.run(LibraryServiceApplication.class, args);
	}

}
