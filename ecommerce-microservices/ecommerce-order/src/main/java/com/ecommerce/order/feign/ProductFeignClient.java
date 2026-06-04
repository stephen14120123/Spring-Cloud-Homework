package com.ecommerce.order.feign;

import com.ecommerce.common.model.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign 远程调用客户端 —— 调用 ecommerce-product 服务的接口
 * <p>
 * name / value   : 目标服务的 spring.application.name（大小写不敏感）
 * fallback       : 降级实现类，由 Spring 容器管理
 */
@FeignClient(name = "ecommerce-product", fallback = ProductFeignFallback.class)
public interface ProductFeignClient {

    /**
     * 映射商品服务的 GET /product/info/{productId}
     * 路径和参数签名必须与提供方完全一致
     */
    @GetMapping("/product/info/{productId}")
    R<String> getProductInfo(@PathVariable("productId") String productId);
}
