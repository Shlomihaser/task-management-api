package com.example.taskmanagement.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        return new MockJwtDecoder();
    }


    private static class MockJwtDecoder implements JwtDecoder {
        
        @Override
        public Jwt decode(String token) throws JwtException {
            // Create a mock JWT with test claims
            Map<String, Object> headers = new HashMap<>();
            headers.put("alg", "RS256");
            headers.put("typ", "JWT");
            
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", "test-user-123");
            claims.put("email", "test@example.com");
            claims.put("cognito:username", "testuser");
            claims.put("cognito:groups", java.util.Arrays.asList("admins"));
            claims.put("iss", "test-issuer");
            claims.put("aud", "test-audience");
            claims.put("iat", Instant.now().getEpochSecond());
            claims.put("exp", Instant.now().plusSeconds(3600).getEpochSecond());
            
            return new Jwt(
                token,
                Instant.now(),
                Instant.now().plusSeconds(3600),
                headers,
                claims
            );
        }
    }
} 