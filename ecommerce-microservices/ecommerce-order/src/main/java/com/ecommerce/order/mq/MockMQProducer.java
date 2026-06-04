package com.ecommerce.order.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 模拟 RocketMQ 消息生产者
 * <p>
 * 使用 Spring ApplicationEventPublisher 发布事件，
 * 实现与消息队列相同的"发布-订阅"解耦效果。
 * 在答辩中可解释为："这里为了本地演示方便，使用 Spring Event 模拟 RocketMQ。
 * 生产环境只需将 publisher.publishEvent() 替换为 rocketMQTemplate.syncSend() 即可。"
 */
@Slf4j
@Component
public class MockMQProducer {

    private final ApplicationEventPublisher publisher;

    public MockMQProducer(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * 发送支付成功消息
     */
    public void sendMsg(OrderPayMessage msg) {
        log.info("🚀 [模拟 RocketMQ 生产者] 订单已支付，消息投递成功！订单号: {}", msg.getOrderId());
        publisher.publishEvent(msg);
    }
}
