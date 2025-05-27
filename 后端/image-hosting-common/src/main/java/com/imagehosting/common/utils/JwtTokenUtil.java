package com.imagehosting.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtTokenUtil {
    /**
     * JWT密钥
     */
    @Value("${jwt.secret:imageHostingSecret}")
    private String secret;

    /**
     * JWT过期时间（秒）
     */
    @Value("${jwt.expiration:86400}")
    private Long expiration;

    /**
     * JWT头部
     */
    @Value("${jwt.tokenHead:Bearer}")
    private String tokenHead;

    /**
     * 生成JWT密钥
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 根据用户信息生成token
     *
     * @param username 用户名
     * @param userId   用户ID
     * @return token
     */
    public String generateToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put("sub", username);
        claims.put("userId", userId);
        claims.put("created", new Date());
        return generateToken(claims);
    }

    /**
     * 从token中获取登录用户名
     *
     * @param token token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            log.error("从token中获取用户名失败", e);
            username = null;
        }
        return username;
    }

    /**
     * 从token中获取用户ID
     *
     * @param token token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Long userId;
        try {
            Claims claims = getClaimsFromToken(token);
            userId = Long.valueOf(claims.get("userId").toString());
        } catch (Exception e) {
            log.error("从token中获取用户ID失败", e);
            userId = null;
        }
        return userId;
    }

    /**
     * 验证token是否有效
     *
     * @param token    token
     * @param username 用户名
     * @return 是否有效
     */
    public boolean validateToken(String token, String username) {
        String tokenUsername = getUsernameFromToken(token);
        return tokenUsername != null && tokenUsername.equals(username) && !isTokenExpired(token);
    }

    /**
     * 判断token是否已经失效
     *
     * @param token token
     * @return 是否失效
     */
    private boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        return expiredDate.before(new Date());
    }

    /**
     * 从token中获取过期时间
     *
     * @param token token
     * @return 过期时间
     */
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 从token中获取JWT的负载
     *
     * @param token token
     * @return 负载
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("JWT格式验证失败", e);
        }
        return claims;
    }

    /**
     * 根据负载生成JWT的token
     *
     * @param claims 负载
     * @return token
     */
    private String generateToken(Map<String, Object> claims) {
        Date createdTime = (Date) claims.get("created");
        Date expirationDate = new Date(createdTime.getTime() + expiration * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从请求头中获取token
     *
     * @param header 请求头
     * @return token
     */
    public String getTokenFromHeader(String header) {
        if (StrUtil.isBlank(header)) {
            return null;
        }
        if (header.startsWith(tokenHead)) {
            return header.substring(tokenHead.length()).trim();
        }
        return null;
    }

    /**
     * 获取token的剩余有效期（秒）
     *
     * @param token token
     * @return 剩余有效期
     */
    public long getTokenRemainingTime(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        return (expiredDate.getTime() - DateUtil.date().getTime()) / 1000;
    }
} 