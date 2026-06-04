package com.ecommerce.order.feign;

import com.ecommerce.common.model.R;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StockFeignFallback implements StockFeignClient {

    @Override
    public R<String> deduct(Map<String, Object> params) {
        return R.fail("库存服务繁忙，触发降级保护");
    }
}
