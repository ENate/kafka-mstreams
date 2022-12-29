package com.minejava.kafkastreams.authorizationserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;


@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class DefaultSecurityConfig {
    

    @Bean
    SecurityFilterChain securityFilterChainAuth(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth ->
            auth.anyRequest().authenticated())
            .formLogin(withDefaults());
        return http.build();
    }

    // Define password encoder
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
