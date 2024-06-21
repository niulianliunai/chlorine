package com.chlorine.rabbitmq.delay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class DelaySender {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelaySender.class);

    @Autowired
    private RabbitTemplate template;

    public void send() {
        String message = LocalDateTime.now().toString();
        this.template.convertAndSend("delayExchange","delayQueueARoutingKey", message);
        LOGGER.info(" [x] Sent '{}'", message);
    }

}
