package com.ecommerce.order.mq;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 支付消息体 —— 模拟 RocketMQ 消息体
 * <p>
 * 包含订单 ID，由生产者发布、消费者异步接收处理。
 */
@Data
@AllArgsConstructor
public class OrderPayMessage {

    /** 已支付成功的订单号 */
    private String orderId;
}
