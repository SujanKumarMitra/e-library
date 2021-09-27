package com.github.sujankumarmitra.assetservice.v1.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.context.annotation.Configuration;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Asset Service",
                description = "Service for storing and hosting static files",
                contact = @Contact(
                        name = "Sujan Kumar Mitra",
                        email = "mitrakumarsujan@gmail.com",
                        url = "https://github.com/SujanKumarMitra/e-library"
                ),
                version = "1.0"
        ),
        security = {
                @SecurityRequirement(name = "cookie"),
                @SecurityRequirement(name = "access_token"),
                @SecurityRequirement(name = "Bearer")
        }
)
@Configuration
public class OpenApiConfiguration {
}
