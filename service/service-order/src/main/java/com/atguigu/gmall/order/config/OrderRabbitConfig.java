package com.atguigu.gmall.order.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: wujijun
 * @Description: 延迟消息创建（订单超时取消）
 * @Date Created in 2022-01-20-22:14
 */
@Configuration
public class OrderRabbitConfig {

    /**
     * @ClassName OrderRabbitConfig
     * @Description 创建一个正常的交换机
     * @Author wujijun
     * @Date 2022/1/20 22:23
     * @Param []
     * @Return org.springframework.amqp.core.Exchange
     */
    @Bean("normalExchange")
    public Exchange normalExchange(){
        return ExchangeBuilder.directExchange("normal_exchange").build();
    }

    /**
     * @ClassName OrderRabbitConfig
     * @Description 创建死信交换机
     * @Author wujijun
     * @Date 2022/1/20 22:36
     * @Param []
     * @Return org.springframework.amqp.core.Exchange
     */
    @Bean("dieExchange")
    public Exchange dieExchange(){
        return ExchangeBuilder.directExchange("die_exchange").build();
    }

    /**
     * @ClassName OrderRabbitConfig
     * @Description 创建一个正常的队列
     * @Author wujijun
     * @Date 2022/1/20 22:25
     * @Param []
     * @Return org.springframework.amqp.core.Queue
     */
    @Bean("normalQueue")
    public Queue normalQueue(){
        return QueueBuilder.durable("normal_queue")
                .withArgument("x-dead-letter-exchange", "die_exchange")
                .withArgument("x-dead-letter-routing-key","order.die")
                .build();
    }

    /**
     * @ClassName OrderRabbitConfig
     * @Description 私信队列
     * @Author wujijun
     * @Date 2022/1/20 22:40
     * @Param []
     * @Return org.springframework.amqp.core.Queue
     */
    @Bean("dieQueue")
    public Queue dieQueue(){
        return QueueBuilder.durable("die_queue").build();
    }

    /**
     * @ClassName OrderRabbitConfig
     * @Description 创建绑定
     * @Author wujijun
     * @Date 2022/1/20 22:31
     * @Param [normalExchange, normalQueue]
     * @Return org.springframework.amqp.core.Binding
     */
    @Bean
    public Binding normalBinding(@Qualifier("normalExchange") Exchange normalExchange,
                                 @Qualifier("normalQueue") Queue normalQueue){
        return BindingBuilder.bind(normalQueue).to(normalExchange).with("order.timeout").noargs();
    }

    /**
     * @ClassName OrderRabbitConfig
     * @Description 死信交换机和死信队列的绑定
     * @Author wujijun
     * @Date 2022/1/20 22:41
     * @Param [dieExchange, dieQueue]
     * @Return org.springframework.amqp.core.Binding
     */
    @Bean
    public Binding dieBinding(@Qualifier("dieExchange") Exchange dieExchange,
                                 @Qualifier("dieQueue") Queue dieQueue){
        return BindingBuilder.bind(dieQueue).to(dieExchange).with("order.die").noargs();
    }
}
