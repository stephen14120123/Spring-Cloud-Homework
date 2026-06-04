package com.ecommerce.cart.controller;
import com.ecommerce.common.model.R;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/cart")
public class CartController {
    @GetMapping("/ping")
    public R<String> ping() {
        return R.ok("购物车服务已成功启动并接入网关");
    }
}
