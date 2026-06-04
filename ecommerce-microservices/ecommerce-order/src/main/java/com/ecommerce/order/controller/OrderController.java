package com.ecommerce.order.controller;

import com.ecommerce.common.model.R;
import com.ecommerce.order.entity.OrderInfo;
import com.ecommerce.order.feign.PaymentFeignClient;
import com.ecommerce.order.feign.ProductFeignClient;
import com.ecommerce.order.feign.StockFeignClient;
import com.ecommerce.order.mapper.OrderInfoMapper;
import com.ecommerce.order.mq.MockMQProducer;
import com.ecommerce.order.mq.OrderPayMessage;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private final OrderInfoMapper orderInfoMapper;

    public OrderController(ProductFeignClient productFeignClient,
                           StockFeignClient stockFeignClient,
                           PaymentFeignClient paymentFeignClient,
                           MockMQProducer mockMQProducer,
                           OrderInfoMapper orderInfoMapper) {
        this.productFeignClient = productFeignClient;
        this.stockFeignClient = stockFeignClient;
        this.paymentFeignClient = paymentFeignClient;
        this.mockMQProducer = mockMQProducer;
        this.orderInfoMapper = orderInfoMapper;
    }

    /**
     * POST /order/create
     * <p>
     * 全链路下单演示（6 步：商品 → 库存 → 支付 → MQ → 落库 → 返回）：
     * <pre>
     * 第一步 → ProductFeign      查询商品信息                 ← Feign 同步
     * 第二步 → StockFeign        扣减库存                     ← Feign 同步
     * 第三步 → PaymentFeign      模拟支付                     ← Feign 同步
     * 第四步 → MockMQProducer    发布支付成功消息              ← 异步
     * 第五步 → OrderInfoMapper   订单数据写入 MySQL            ← 同步
     * 第六步 → 返回响应          消费者异步处理物流/积分        ← 不阻塞
     * </pre>
     * <p>
     * 请求体示例：{"productId": "101", "quantity": 1}
     */
    @PostMapping("/create")
    public R<String> createOrder(@RequestBody Map<String, Object> params) {
        Long productId = Long.valueOf(String.valueOf(params.get("productId")));
        int quantity = Integer.parseInt(String.valueOf(params.get("quantity")));

        // ========== 第一步：远程调用商品服务 ==========
        R<String> productResult = productFeignClient.getProductInfo(String.valueOf(productId));
        if (!productResult.isSuccess()) {
            return productResult;
        }
        String productInfo = productResult.getData();

        // ========== 第二步：远程调用库存服务扣减库存 ==========
        Map<String, Object> stockParams = new HashMap<>();
        stockParams.put("productId", String.valueOf(productId));
        stockParams.put("quantity", String.valueOf(quantity));

        R<String> stockResult = stockFeignClient.deduct(stockParams);
        if (!stockResult.isSuccess()) {
            return stockResult;
        }

        // ========== 第三步：远程调用支付服务 ==========
        String orderNo = "ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Map<String, Object> payParams = new HashMap<>();
        payParams.put("orderId", orderNo);
        payParams.put("amount", 7999);

        R<String> payResult = paymentFeignClient.pay(payParams);
        if (!payResult.isSuccess()) {
            return payResult;
        }
        String payInfo = payResult.getData();

        // ========== 第四步：模拟 MQ 异步发送支付成功消息 ==========
        mockMQProducer.sendMsg(new OrderPayMessage(orderNo));

        // ========== 第五步：订单数据写入 MySQL ==========
        OrderInfo order = new OrderInfo();
        order.setOrderNo(orderNo);
        order.setProductId(productId);
        order.setAmount(new BigDecimal("7999.00"));
        order.setStatus(0);                     // 0-已创建
        order.setCreateTime(LocalDateTime.now());

        orderInfoMapper.insert(order);

        // ========== 第六步：返回结果 ==========
        String msg = "全链路大满贯！商品信息: " + productInfo
                + " | " + payInfo
                + " | 订单已写入数据库，单号: " + orderNo;
        return R.ok(msg);
    }
}
