package com.ecommerce.order.controller;

import com.ecommerce.common.model.R;
import com.ecommerce.order.feign.PaymentFeignClient;
import com.ecommerce.order.feign.ProductFeignClient;
import com.ecommerce.order.feign.StockFeignClient;
import com.ecommerce.order.mq.MockMQProducer;
import com.ecommerce.order.mq.OrderPayMessage;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final ProductFeignClient productFeignClient;
    private final StockFeignClient stockFeignClient;
    private final PaymentFeignClient paymentFeignClient;
    private final MockMQProducer mockMQProducer;

    public OrderController(ProductFeignClient productFeignClient,
                           StockFeignClient stockFeignClient,
                           PaymentFeignClient paymentFeignClient,
                           MockMQProducer mockMQProducer) {
        this.productFeignClient = productFeignClient;
        this.stockFeignClient = stockFeignClient;
        this.paymentFeignClient = paymentFeignClient;
        this.mockMQProducer = mockMQProducer;
    }

    /**
     * POST /order/create
     * <p>
     * 全链路下单演示（同步 + 异步）：
     * <pre>
     * 第一步 → ProductFeign      查询商品信息          ← Feign 同步
     * 第二步 → StockFeign        扣减库存              ← Feign 同步
     * 第三步 → PaymentFeign      模拟支付              ← Feign 同步
     * 第四步 → MockMQProducer    发布支付成功消息       ← ApplicationEvent 异步解耦
     * 第五步 → MockMQConsumer    @Async 异步消费       ← 物流/积分等（不阻塞返回）
     * </pre>
     * <p>
     * 请求体示例：{"productId": "101", "quantity": 1}
     */
    @PostMapping("/create")
    public R<String> createOrder(@RequestBody Map<String, Object> params) {
        String productId = String.valueOf(params.get("productId"));
        int quantity = Integer.parseInt(String.valueOf(params.get("quantity")));

        // ========== 第一步：远程调用商品服务 ==========
        R<String> productResult = productFeignClient.getProductInfo(productId);
        if (!productResult.isSuccess()) {
            return productResult;
        }
        String productInfo = productResult.getData();

        // ========== 第二步：远程调用库存服务扣减库存 ==========
        Map<String, Object> stockParams = new HashMap<>();
        stockParams.put("productId", productId);
        stockParams.put("quantity", String.valueOf(quantity));

        R<String> stockResult = stockFeignClient.deduct(stockParams);
        if (!stockResult.isSuccess()) {
            return stockResult;
        }

        // ========== 第三步：远程调用支付服务 ==========
        String orderId = "ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Map<String, Object> payParams = new HashMap<>();
        payParams.put("orderId", orderId);
        payParams.put("amount", 7999);

        R<String> payResult = paymentFeignClient.pay(payParams);
        if (!payResult.isSuccess()) {
            return payResult;
        }
        String payInfo = payResult.getData();

        // ========== 第四步：模拟 MQ 异步发送支付成功消息 ==========
        // 解释：这里使用 Spring Event 模拟 RocketMQ 发送消息。
        // 生产环境只需替换为 rocketMQTemplate.syncSend("pay-topic", msg) 即可。
        mockMQProducer.sendMsg(new OrderPayMessage(orderId));

        // ========== 第五步：返回结果（消费者异步处理，不阻塞此响应）==========
        String msg = "全链路大满贯！商品信息: " + productInfo + " | " + payInfo + " | 物流已异步通知发货。";
        return R.ok(msg);
    }
}
