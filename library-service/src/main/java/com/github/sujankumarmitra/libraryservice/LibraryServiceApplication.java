package com.github.sujankumarmitra.libraryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
public class LibraryServiceApplication {

	public static void main(String[] args) {
		ReactorDebugAgent.init();
		SpringApplication.run(LibraryServiceApplication.class, args);
	}

}
