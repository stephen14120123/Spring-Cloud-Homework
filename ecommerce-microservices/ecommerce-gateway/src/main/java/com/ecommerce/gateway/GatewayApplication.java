package com.ecommerce.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关模块启动类
 * <p>
 * @SpringBootApplication          —— Spring Boot 核心注解
 * @EnableDiscoveryClient          —— 开启 Nacos 服务发现，让 Gateway 能从 Nacos 拉取服务列表
 * <p>
 * 启动后监听 8080 端口，所有前端请求统一经过此网关进行路由转发和 JWT 鉴权。
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
