package com.github.sujankumarmitra.ebookprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
public class EbookProcessorApplication {

	public static void main(String[] args) {
		ReactorDebugAgent.init();
		SpringApplication.run(EbookProcessorApplication.class, args);
	}

}
