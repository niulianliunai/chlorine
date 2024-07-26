package com.chlorine.rabbitmq.topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class TopicSender {
    private static final String exchangeName = "exchange.topic";
    private static final Logger LOGGER = LoggerFactory.getLogger(TopicSender.class);
    private final String[] keys = {"quick.orange.rabbit", "lazy.orange.elephant", "quick.orange.fox",
            "lazy.brown.fox", "lazy.pink.rabbit", "quick.brown.fox"};
    @Autowired
    private RabbitTemplate template;

    public void send(int index) {
        int limitIndex = index % keys.length;
        String key = keys[limitIndex];
        String message = "Hello to " + key + ' ' +
                (index + 1);
        template.convertAndSend(exchangeName, key, message);
        LOGGER.info(" [x] Sent '{}'", message);
        System.out.println(" [x] Sent '" + message + "'");
    }
}
