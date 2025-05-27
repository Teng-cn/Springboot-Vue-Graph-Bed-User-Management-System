package com.imagehosting.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

/**
 * JWT工具类
 */
@Data
@Component
public class JwtUtil {

    /**
     * 密钥
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * 过期时间（毫秒）
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 请求头
     */
    @Value("${jwt.header}")
    private String header;

    /**
     * 令牌前缀
     */
    @Value("${jwt.tokenPrefix}")
    private String tokenPrefix;

    /**
     * 密钥对象
     */
    private Key key;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    /**
     * 生成令牌
     *
     * @param userId 用户ID
     * @return 令牌
     */
    public String generateToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 验证令牌
     *
     * @param token 令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从令牌中获取用户ID
     *
     * @param token 令牌
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
} 