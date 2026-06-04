package com.ecommerce.admin.controller;
import com.ecommerce.common.model.R;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/ping")
    public R<String> ping() {
        return R.ok("后台管理服务已成功启动并接入网关");
    }
}
