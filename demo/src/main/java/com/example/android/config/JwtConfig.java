package com.example.android.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    
    @Value("${jwt.secret-key}")
    private String secretKey;
    
    @Value("${jwt.expiration-time:86400000}")
    private long expirationTime;
    
    public String getSecretKey() {
        return secretKey;
    }
    
    public long getExpirationTime() {
        return expirationTime;
    }
}