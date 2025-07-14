package com.example.demo.utils;

import com.example.demo.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final Duration expirationTime;

    public JwtUtil(JwtConfig jwtConfig) {
        this.secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8));
        this.expirationTime = Duration.ofMillis(jwtConfig.getExpirationTime());
    }

    public String generateToken(Integer userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(Date.from(now))          // 使用 Instant 类型
                .expiration(Date.from(now.plus(expirationTime)))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public Integer parseToken(String token) {
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

        // 获取时间字段的正确方式
        Date expiration = jws.getPayload().getExpiration();
        // 如果需要使用 Instant 可以转换：
        Instant expirationInstant = expiration.toInstant();

        return Integer.parseInt(jws.getPayload().getSubject());
    }
}
