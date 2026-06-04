package com.ecommerce.gateway.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * 极简版 JWT 工具类
 * <p>
 * 从 application.yml 读取 jwt.secret 作为签名密钥，
 * 提供 Token 校验和 userId 提取两个基础方法。
 * 网关层面的过滤器仅需要 "验证" 和 "解析"，不需要 "签发" ——
 * 签发由 ecommerce-auth 服务在用户登录/注册成功后完成。
 */
@Component
public class JwtUtil {

    /** 从配置读取的签名密钥字符串 */
    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        // 将字符串密钥转为 JJWT 所需的 SecretKey 对象
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 校验 Token 是否合法
     *
     * @param token 原始 JWT 字符串（不含 "Bearer " 前缀）
     * @return true=合法且未过期；false=无效或已过期
     */
    public boolean verifyToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 从合法 Token 中提取用户 ID
     *
     * @param token 原始 JWT 字符串
     * @return 用户 ID（字符串类型，可根据业务换成 Long）
     * @throws JwtException 如果 Token 无效则抛出异常
     */
    public String getUserIdFromToken(String token) {
        return parseToken(token)
                .getBody()
                .get("userId", String.class);
    }

    /** 内部方法：解析 Token 并返回 Claims */
    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }
}
