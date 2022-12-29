package com.minejava.kafkastreams.authorizationserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import com.minejava.kafkastreams.authorizationserver.security.OidcUserInfoService;

import static org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames.ID_TOKEN;
import static org.springframework.security.oauth2.server.authorization.OAuth2TokenType.ACCESS_TOKEN;


@Configuration
public class JwtTokenCustomizerConfig {
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(OidcUserInfoService userInfoService) {
        return (context) -> {
            if (ID_TOKEN.equals(context.getTokenType().getValue()) || ACCESS_TOKEN.equals(context.getTokenType())) {
                OidcUserInfo userInfo = userInfoService.loadUser(
                        context.getPrincipal().getName());
                context.getClaims().claims(claims ->
                        claims.putAll(userInfo.getClaims()));
            }
        };
    }
}
