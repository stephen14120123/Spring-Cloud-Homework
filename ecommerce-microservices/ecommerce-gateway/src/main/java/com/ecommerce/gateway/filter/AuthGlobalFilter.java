package com.ecommerce.gateway.filter;

import com.ecommerce.gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 全局网关鉴权过滤器
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    // 1. 配置白名单：直接放行登录注册，以及 Knife4j/Swagger 接口文档相关的静态资源
    private static final String[] WHITE_LIST = {
            "/api/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/swagger-resources/**",
            "/webjars/**",
            "/doc.html"
    };

    public AuthGlobalFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();

        // 2. 放行 OPTIONS 跨域预检请求，防止前端报跨域错误
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // 3. 白名单路径直接放行
        for (String pattern : WHITE_LIST) {
            if (antPathMatcher.match(pattern, path)) {
                return chain.filter(exchange);
            }
        }

        // 4. 获取 Authorization 请求头
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // 5. 核心修复：截取前端通常会带的 "Bearer " 前缀 (含空格一共7个字符)
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 6. 校验 Token
        if (token == null || !jwtUtil.verifyToken(token)) {
            return unauthorizedResponse(response, "无效的Token或Token已过期");
        }

        // 7. 鉴权成功，解析 userId 并塞入 Header 透传给下游微服务
        String userId = jwtUtil.getUserIdFromToken(token);
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", userId)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    /**
     * WebFlux 响应式环境下的 JSON 错误返回标准写法
     */
    private Mono<Void> unauthorizedResponse(ServerHttpResponse response, String msg) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String json = String.format("{\"code\": 401, \"msg\": \"%s\"}", msg);
        // 使用 BufferFactory 包装字节流
        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        // 设置较高的优先级，保证在路由转发前执行
        return -100;
    }
}