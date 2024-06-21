package com.chlorine.rabbitmq.delay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.time.LocalDateTime;


public class DelayReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayReceiver.class);

    @RabbitListener(queues = "deadLetterQueueA")
    public void receiveA(Message message) {
        String msg = new String(message.getBody());
        LOGGER.info("当前时间：{},死信队列A收到消息：{}", LocalDateTime.now(), msg);
    }
}
