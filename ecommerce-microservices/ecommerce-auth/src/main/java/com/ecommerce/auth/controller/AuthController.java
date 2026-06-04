package com.ecommerce.auth.controller;

import com.ecommerce.common.model.R;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * 认证控制器
 * <p>
 * 模拟登录接口：校验账号密码 → 签发 JWT → 返回 Token。
 * 前端拿到 Token 后在后续请求的 Authorization 头中携带。
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SecretKey secretKey;
    private final long expiration;

    /** 从 yml 注入 JWT 密钥和过期时间 */
    public AuthController(@Value("${jwt.secret}") String secret,
                          @Value("${jwt.expiration}") long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /**
     * 模拟登录
     * <p>
     * 请求示例：
     * POST /auth/login
     * { "username": "admin", "password": "123456" }
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        // 模拟校验（演示用，正式项目应查数据库）
        if (!"admin".equals(username) || !"123456".equals(password)) {
            return R.fail("账号或密码错误");
        }

        // 签发 JWT Token，Payload 中放入 userId
        Date now = new Date();
        String token = Jwts.builder()
                .claim("userId", "1001")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(secretKey)
                .compact();

        return R.ok("登录成功", token);
    }
}
