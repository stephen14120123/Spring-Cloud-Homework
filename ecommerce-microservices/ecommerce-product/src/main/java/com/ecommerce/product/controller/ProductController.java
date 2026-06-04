package com.ecommerce.product.controller;

import com.alibaba.fastjson2.JSON;
import com.ecommerce.common.model.R;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.mapper.ProductMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductMapper productMapper;
    private final StringRedisTemplate redisTemplate;

    public ProductController(ProductMapper productMapper, StringRedisTemplate redisTemplate) {
        this.productMapper = productMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * GET /product/info/{productId}
     * <p>
     * 旁路缓存（Cache-Aside）模式：
     * 1. 先查 Redis 缓存（key = "product:info:{id}"）
     * 2. 命中 → 直接返回缓存数据（极速响应）
     * 3. 未命中 → 查 MySQL
     *    a. MySQL 有数据 → 写入 Redis（过期时间 1 小时），返回
     *    b. MySQL 无数据 → 返回"商品不存在"
     * <p>
     * 这种模式在答辩中可以解释为：
     * "读请求优先走缓存，缓存 miss 时才回源数据库，并设置 TTL 保证最终一致性。
     *  高并发场景下可以有效保护数据库，将热点商品的 QPS 从 DB 层转移到 Redis 层。"
     */
    @GetMapping("/info/{productId}")
    public R<String> getProductInfo(@PathVariable String productId) {
        String cacheKey = "product:info:" + productId;

        // ---------- 第一步：查询 Redis 缓存 ----------
        String cachedJson = redisTemplate.opsForValue().get(cacheKey);
        if (cachedJson != null) {
            // 缓存命中，直接返回
            return R.ok("【Redis 缓存】" + cachedJson);
        }

        // ---------- 第二步：缓存 miss，查询 MySQL ----------
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return R.fail("商品不存在");
        }

        // 将查询结果组装成展示字符串
        String productInfo = "商品ID: " + product.getId()
                + " —— " + product.getName()
                + ", 价格: " + product.getPrice();

        // ---------- 第三步：回写 Redis 缓存，设置 1 小时过期 ----------
        // 下次查询同样商品时直接走缓存，不再穿透到数据库
        redisTemplate.opsForValue().set(cacheKey, productInfo, 1, TimeUnit.HOURS);

        return R.ok("【MySQL 查询】" + productInfo);
    }
}
