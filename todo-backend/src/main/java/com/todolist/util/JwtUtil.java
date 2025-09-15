package com.todolist.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT工具类
 * 
 * 负责JWT token的生成、解析和验证
 * 
 * @author todolist
 * @version 1.0
 * @since 2025-09-12
 */
@Component
public class JwtUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    @Value("${jwt.secret:mySecretKey123456789012345678901234567890}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}") // 24小时
    private Long expiration;
    
    /**
     * 生成JWT token
     * 
     * @param username 用户名
     * @return JWT token字符串
     */
    public String generateToken(String username) {
        logger.info("=== 生成JWT token开始 ===");
        logger.info("用户名: {}", username);
        logger.info("JWT secret长度: {}", secret.length());
        logger.info("JWT过期时间(毫秒): {}", expiration);
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        
        String token = Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
        
        logger.info("生成的token: {}", token.substring(0, Math.min(50, token.length())) + "...");
        logger.info("=== 生成JWT token完成 ===");
        
        return token;
    }
    
    /**
     * 从token中获取用户名
     * 
     * @param token JWT token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        logger.info("=== 从token获取用户名开始 ===");
        logger.info("输入token: {}", token.substring(0, Math.min(50, token.length())) + "...");
        
        try {
            Claims claims = getClaimsFromToken(token);
            String username = claims.getSubject();
            logger.info("解析出的用户名: {}", username);
            logger.info("=== 从token获取用户名完成 ===");
            return username;
        } catch (Exception e) {
            logger.error("从token获取用户名失败: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 验证token是否有效
     * 
     * @param token JWT token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        logger.info("=== 验证JWT token开始 ===");
        logger.info("输入token: {}", token.substring(0, Math.min(50, token.length())) + "...");
        logger.info("当前JWT secret: {}", secret.substring(0, Math.min(20, secret.length())) + "...");
        
        try {
            Claims claims = getClaimsFromToken(token);
            logger.info("成功解析Claims: {}", claims);
            
            boolean isExpired = isTokenExpired(claims);
            logger.info("token是否过期: {}", isExpired);
            
            boolean isValid = !isExpired;
            logger.info("token是否有效: {}", isValid);
            logger.info("=== 验证JWT token完成 ===");
            
            return isValid;
        } catch (Exception e) {
            logger.error("JWT token验证失败: {}", e.getMessage(), e);
            logger.info("=== 验证JWT token完成(失败) ===");
            return false;
        }
    }
    
    /**
     * 从token中获取Claims
     * 
     * @param token JWT token
     * @return Claims对象
     */
    private Claims getClaimsFromToken(String token) {
        logger.info("=== 解析JWT Claims开始 ===");
        logger.info("输入token: {}", token.substring(0, Math.min(50, token.length())) + "...");
        
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
            logger.info("使用secret创建密钥: {}", secret.substring(0, Math.min(20, secret.length())) + "...");
            
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            logger.info("成功解析Claims: {}", claims);
            logger.info("=== 解析JWT Claims完成 ===");
            
            return claims;
        } catch (Exception e) {
            logger.error("解析JWT Claims失败: {}", e.getMessage(), e);
            logger.info("=== 解析JWT Claims完成(失败) ===");
            throw e;
        }
    }
    
    /**
     * 检查token是否过期
     * 
     * @param claims Claims对象
     * @return 是否过期
     */
    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        Date now = new Date();
        boolean expired = expiration.before(now);
        
        logger.info("token过期时间: {}", expiration);
        logger.info("当前时间: {}", now);
        logger.info("是否过期: {}", expired);
        
        return expired;
    }
}
