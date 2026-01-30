package com.easyarticle.util;

import com.easyarticle.constant.Constants;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具类
 * 用于生成、解析和验证JWT令牌
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;
    
    private byte[] secretBytes;
    
    @PostConstruct
    public void init() {
        // 确保密钥长度足够，至少32字节用于HS256
        if (secret.length() < 32) {
            // 使用密钥扩展技术确保足够的长度
            StringBuilder extendedSecret = new StringBuilder(secret);
            while (extendedSecret.length() < 32) {
                extendedSecret.append(secret);
            }
            secret = extendedSecret.toString().substring(0, 32);
        }
        secretBytes = secret.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 从token中获取用户名
     * @param token JWT令牌
     * @return String 用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从token中获取过期时间
     * @param token JWT令牌
     * @return Date 过期时间
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 从token中获取指定的声明
     * @param token JWT令牌
     * @param claimsResolver 声明解析器
     * @param <T> 返回类型
     * @return T 解析结果
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从token中获取所有声明
     * @param token JWT令牌
     * @return Claims 所有声明
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretBytes)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException ex) {
            System.out.println("Invalid JWT signature: " + ex.getMessage());
            throw new MalformedJwtException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            System.out.println("JWT token is expired: " + ex.getMessage());
            throw new ExpiredJwtException(null, null, "Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            System.out.println("JWT token is unsupported: " + ex.getMessage());
            throw new UnsupportedJwtException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT claims string is empty: " + ex.getMessage());
            throw new IllegalArgumentException("JWT claims string is empty");
        } catch (Exception ex) {
            System.out.println("Error parsing JWT token: " + ex.getMessage());
            throw new RuntimeException("Error parsing JWT token", ex);
        }
    }

    /**
     * 检查token是否过期
     * @param token JWT令牌
     * @return Boolean 是否过期
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 生成token
     * @param userDetails 用户详情
     * @return String JWT令牌
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * 生成token，包含用户ID
     * @param userDetails 用户详情
     * @param userId 用户ID
     * @return String JWT令牌
     */
    public String generateToken(UserDetails userDetails, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.JWT.USER_ID_CLAIM, userId);
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * 从token中获取用户ID
     * @param token JWT令牌
     * @return Long 用户ID
     */
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get(Constants.JWT.USER_ID_CLAIM, Long.class);
    }

    /**
     * 创建token
     * @param claims 声明
     * @param subject 主题
     * @return String JWT令牌
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    /**
     * 验证token
     * @param token JWT令牌
     * @param userDetails 用户详情
     * @return Boolean 是否有效
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
