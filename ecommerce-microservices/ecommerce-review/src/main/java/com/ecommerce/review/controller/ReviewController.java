package com.ecommerce.review.controller;
import com.ecommerce.common.model.R;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/review")
public class ReviewController {
    @GetMapping("/ping")
    public R<String> ping() {
        return R.ok("评价服务已成功启动并接入网关");
    }
}
