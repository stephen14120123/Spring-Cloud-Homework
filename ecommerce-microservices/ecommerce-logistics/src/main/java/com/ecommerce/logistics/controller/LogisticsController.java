package com.ecommerce.logistics.controller;
import com.ecommerce.common.model.R;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/logistics")
public class LogisticsController {
    @GetMapping("/ping")
    public R<String> ping() {
        return R.ok("物流服务已成功启动并接入网关");
    }
}
