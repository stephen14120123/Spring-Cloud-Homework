package com.ecommerce.product.controller;

import com.ecommerce.common.model.R;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    /**
     * GET /product/info/{productId}
     * 模拟返回商品信息（纯内存，不查数据库）
     */
    @GetMapping("/info/{productId}")
    public R<String> getProductInfo(@PathVariable String productId) {
        String info = "商品ID: " + productId + " —— iPhone 15 Pro, 价格: 7999";
        return R.ok(info);
    }
}
