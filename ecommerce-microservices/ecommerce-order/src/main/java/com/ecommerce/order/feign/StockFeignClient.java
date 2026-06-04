package com.ecommerce.order.feign;

import com.ecommerce.common.model.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "ecommerce-stock", fallback = StockFeignFallback.class)
public interface StockFeignClient {

    /**
     * 调用库存服务的 POST /stock/deduct
     */
    @PostMapping("/stock/deduct")
    R<String> deduct(@RequestBody Map<String, Object> params);
}
