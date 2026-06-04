package com.ecommerce.order.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 模拟 RocketMQ 消息消费者
 * <p>
 * @Async          —— 异步执行，不阻塞主线程（模拟 MQ 的异步消费模型）
 * @EventListener  —— 监听 OrderPayMessage 事件，由 ApplicationEventPublisher 触发
 * <p>
 * 在答辩中可解释为："消费者收到支付成功的消息后，执行物流发货、积分增加等
 * 耗时的下游业务。由于使用 @Async 异步，这些操作不会影响用户的下单响应速度。"
 */
@Slf4j
@Component
public class MockMQConsumer {

    @Async
    @EventListener
    public void handlePayMessage(OrderPayMessage msg) throws InterruptedException {
        // 模拟网络延迟 / 消息拉取间隔
        Thread.sleep(2000);

        log.info("📥 [模拟 RocketMQ 消费者] 收到支付成功消息，开始异步处理：通知物流发货、给用户增加积分... 订单号: {}", msg.getOrderId());
        log.info("📦 物流系统已接收发货通知，订单 {} 准备出库", msg.getOrderId());
        log.info("⭐ 用户积分 +100（订单号: {}）", msg.getOrderId());
    }
}
