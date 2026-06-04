package com.ecommerce.user.controller;
import com.ecommerce.common.model.R;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/ping")
    public R<String> ping() {
        return R.ok("用户服务已成功启动并接入网关");
    }
}
