package com.ecommerce.stock.controller;

import com.ecommerce.common.model.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 库存控制器
 * <p>
 * 标注 @RefreshScope 使 Nacos 配置变更时自动刷新 Bean 内的 @Value 属性，
 * 无需重启服务即可生效。
 */
@RefreshScope
@RestController
@RequestMapping("/stock")
public class StockController {

    /**
     * 动态开关：从 Nacos Config 读取 stock.is-open 配置项。
     * 默认值为 true。
     * 在 Nacos 控制台修改该配置项后，Spring 容器会动态刷新此字段的值。
     */
    @Value("${stock.is-open:true}")
    private boolean stockIsOpen;

    /**
     * POST /stock/deduct
     * <p>
     * 模拟库存扣减。如果动态开关为 false 则拒绝扣减，返回维护提示。
     * <p>
     * 请求体示例：{"productId": "101", "quantity": 1}
     */
    @PostMapping("/deduct")
    public R<String> deduct(@RequestBody Map<String, Object> params) {
        if (!stockIsOpen) {
            return R.fail("库存系统维护中，暂停扣减");
        }

        String productId = String.valueOf(params.get("productId"));
        String quantity = String.valueOf(params.get("quantity"));

        String msg = "扣减库存成功 —— 商品: " + productId + ", 数量: " + quantity;
        return R.ok(msg);
    }
}
