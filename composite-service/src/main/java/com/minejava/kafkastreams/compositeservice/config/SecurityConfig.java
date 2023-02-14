package com.minejava.kafkastreams.compositeservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exch -> exch
                .pathMatchers("/users").permitAll()
                .anyExchange().authenticated());
        return http.build();
    }
    
}
