package com.chlorine.rabbitmq.delay;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DelayRabbitConfig {
    @Bean
    public DelaySender delaySender() {
        return new DelaySender();
    }

    @Bean
    public DelayReceiver delayReceiver() {
        return new DelayReceiver();
    }

    @Bean("delayExchange")
    public DirectExchange delayExchange() {
        return new DirectExchange("delayExchange");
    }

    @Bean("delayQueueA")
    public Queue delayQueueA() {
        Map<String, Object> args = new HashMap<>(2);
        // x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", "deadLetterExchange");
        // x-dead-letter-routing-key  这里声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", "deadLetterQueueARoutingKey");
        // x-message-ttl  声明队列的TTL  单位是毫秒 1000*6
        args.put("x-message-ttl", 6000);
        return QueueBuilder.durable("delayQueueA").withArguments(args).build();
    }

    @Bean("deadLetterExchange")
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("deadLetterExchange");
    }

    @Bean("deadLetterQueueA")
    public Queue deadLetterQueueA() {
        return new Queue("deadLetterQueueA");
    }

    /**
     * 将延时队列A与延时交换机绑定   并指定延时队列路由
     *
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding delayBindingA(@Qualifier("delayQueueA") Queue queue,
                                 @Qualifier("delayExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("delayQueueARoutingKey");
    }

    /**
     * 将死信队列 与 死信交换机绑定   指定死信队列路由
     *
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding deadLetterBindingA(@Qualifier("deadLetterQueueA") Queue queue,
                                      @Qualifier("deadLetterExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("deadLetterQueueARoutingKey");
    }

}
