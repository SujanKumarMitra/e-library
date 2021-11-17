package com.github.sujankumarmitra.authorizationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
public class AuthorizationServiceApplication {

    public static void main(String[] args) {
        ReactorDebugAgent.init();
        SpringApplication.run(AuthorizationServiceApplication.class, args);
    }

}
