package com.github.sujankumarmitra.authorizationservice.v1.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Authorization Service",
                description = "Service for issuing and validating JWT tokens",
                contact = @Contact(
                        name = "Sujan Kumar Mitra",
                        email = "mitrakumarsujan@gmail.com",
                        url = "https://github.com/SujanKumarMitra/e-library"
                ),
                version = "1.0"
        )
)
@Configuration
public class OpenAPIConfiguration {
}
