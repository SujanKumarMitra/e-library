package com.github.sujankumarmitra.assetservice.v1.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@AllArgsConstructor
public class SecurityConfiguration {

    ReactiveAuthenticationManager authenticationManager;
    ServerAuthenticationConverter authenticationConverter;

    @Bean
    public SecurityWebFilterChain jwtFilterChain(ServerHttpSecurity httpSecurity) {

        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(authenticationManager);
        jwtFilter.setServerAuthenticationConverter(authenticationConverter);


//        @formatter:off
        return httpSecurity
                .securityMatcher(pathMatchers("/api/**"))
                .authorizeExchange()
                    .anyExchange()
                        .authenticated()
                .and()
                    .httpBasic().disable()
                    .csrf().disable()
                    .logout().disable()
                    .addFilterAt(jwtFilter, AUTHENTICATION)
                .build();
//        @formatter:on
    }

    @Bean
    @Order(HIGHEST_PRECEDENCE)
    public SecurityWebFilterChain swaggerFilter(ServerHttpSecurity httpSecurity) {
//        @formatter:off

        return httpSecurity
                .securityMatcher(getPathMatchers())
                .authorizeExchange()
                    .anyExchange()
                    .permitAll()
                .and()
                    .httpBasic().disable()
                    .logout().disable()
                    .csrf().disable()
                .build();

        //@formatter:on

    }

    private ServerWebExchangeMatcher getPathMatchers() {
        return pathMatchers(
                "/v3/api-docs/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/webjars/**");
    }

}
