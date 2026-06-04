package com.ecommerce.order.feign;

import com.ecommerce.common.model.R;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentFeignFallback implements PaymentFeignClient {

    @Override
    public R<String> pay(Map<String, Object> params) {
        return R.fail("支付服务繁忙，触发降级保护");
    }
}
