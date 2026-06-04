package com.ecommerce.order.feign;

import com.ecommerce.common.model.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "ecommerce-payment", fallback = PaymentFeignFallback.class)
public interface PaymentFeignClient {

    /**
     * 调用支付服务的 POST /payment/pay
     */
    @PostMapping("/payment/pay")
    R<String> pay(@RequestBody Map<String, Object> params);
}
