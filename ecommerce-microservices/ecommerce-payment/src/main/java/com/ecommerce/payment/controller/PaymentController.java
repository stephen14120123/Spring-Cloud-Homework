package com.ecommerce.payment.controller;

import com.ecommerce.common.model.R;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    /**
     * POST /payment/pay
     * <p>
     * 模拟支付接口：接收订单号和金额，模拟支付成功并返回交易流水号。
     * <p>
     * 请求体示例：{"orderId": "ORD20240601001", "amount": 7999}
     */
    @PostMapping("/pay")
    public R<String> pay(@RequestBody Map<String, Object> params) {
        String orderId = String.valueOf(params.get("orderId"));
        Object amount = params.get("amount");

        // 模拟支付处理耗时
        String transactionId = UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();

        String msg = "支付成功，交易流水号: " + transactionId + "，订单: " + orderId + "，金额: " + amount;
        return R.ok(msg);
    }
}
