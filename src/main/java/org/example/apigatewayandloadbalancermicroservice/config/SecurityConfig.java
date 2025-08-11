package org.example.apigatewayandloadbalancermicroservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF for simplicity in API Gateway. Consider enabling for browser-based clients.
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/eureka/**").permitAll() // Allow Eureka traffic
                .anyExchange().authenticated() // All other requests require authentication
            )
            .httpBasic(org.springframework.security.config.Customizer.withDefaults()); // Enable HTTP Basic authentication
        return http.build();
    }

    @Bean
    KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }
}