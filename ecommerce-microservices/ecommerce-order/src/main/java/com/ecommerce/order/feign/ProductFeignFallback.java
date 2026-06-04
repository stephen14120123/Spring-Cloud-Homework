package com.ecommerce.order.feign;

import com.ecommerce.common.model.R;
import org.springframework.stereotype.Component;

/**
 * Feign 降级实现 —— 当商品服务不可用 / 超时时触发
 * <p>
 * 必须标注 @Component 让 Spring 管理，
 * 必须实现对应的 FeignClient 接口。
 */
@Component
public class ProductFeignFallback implements ProductFeignClient {

    @Override
    public R<String> getProductInfo(String productId) {
        return R.fail("商品服务繁忙，触发降级保护");
    }
}
