package com.ecommerce.coupon.controller;
import com.ecommerce.common.model.R;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/coupon")
public class CouponController {
    @GetMapping("/ping")
    public R<String> ping() {
        return R.ok("优惠券服务已成功启动并接入网关");
    }
}
