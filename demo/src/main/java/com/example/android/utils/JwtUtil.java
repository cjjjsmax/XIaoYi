package com.example.android.utils;

import com.example.android.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    //JWT配置类实例
    private final JwtConfig jwtConfig;
    //加密密钥
    private Key key;
    //Token过期时间
    private long expirationTime;

    @Autowired
    public JwtUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @PostConstruct//应用启动后自动执行
    public void init() {
        //从配置类获取密钥
        String secretKey = jwtConfig.getSecretKey();
        //效验密钥有效性
        if (secretKey == null || secretKey.length() < 32) {
            throw new IllegalArgumentException("JWT密钥至少为32字节");
        }
        //将字符密钥转换为加密密钥对象
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        //获取Token过期时间
        this.expirationTime = jwtConfig.getExpirationTime();
    }

    //生成Token
    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //解析Token
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //验证Token
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}