package com.atguigu.gmall.pay.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: wujijun
 * @Description: 创建交换机和队列;通知支付结果的配置类
 * @Date Created in 2022-01-21-22:20
 */
@Configuration
public class PayRabbitConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * @ClassName PayRabbitConfig
     * @Description //创建支付的交换机
     * @Author wujijun
     * @Date 2022/1/21 22:25
     * @Param []
     * @Return org.springframework.amqp.core.Exchange
     */
    @Bean("payExchange")
    public Exchange payExchange(){
        return ExchangeBuilder.directExchange("pay_exchange").build();
    }

    /**
     * @ClassName PayRabbitConfig
     * @Description 创建微信支付的队列
     * @Author wujijun
     * @Date 2022/1/21 22:27
     * @Param []
     * @Return org.springframework.amqp.core.Queue
     */
    @Bean("wxQueue")
    public Queue wxQueue(){
        return QueueBuilder.durable("wx_pay_queue").build();
    }

    /**
     * @ClassName PayRabbitConfig
     * @Description 创建支付宝支付的队列
     * @Author wujijun
     * @Date 2022/1/21 22:27
     * @Param []
     * @Return org.springframework.amqp.core.Queue
     */
    @Bean("zfbQueue")
    public Queue zfbQueue(){
        return QueueBuilder.durable("zfb_pay_queue").build();
    }

    /**
     * @ClassName PayRabbitConfig
     * @Description 绑定微信的支付队列和支付交换机
     * @Author wujijun
     * @Date 2022/1/21 22:32
     * @Param [payExchange, wxQueue]
     * @Return org.springframework.amqp.core.Binding
     */
    @Bean
    public Binding wxBinding(@Qualifier("payExchange") Exchange payExchange,
                             @Qualifier("wxQueue") Queue wxQueue){
        return BindingBuilder.bind(wxQueue).to(payExchange).with("pay.wx").noargs();
    }

    /**
     * @ClassName PayRabbitConfig
     * @Description 绑定支付宝的支付队列和支付交换机
     * @Author wujijun
     * @Date 2022/1/21 22:32
     * @Param [payExchange, zfbQueue]
     * @Return org.springframework.amqp.core.Binding
     */
    @Bean
    public Binding zfbBinding(@Qualifier("payExchange") Exchange payExchange,
                             @Qualifier("zfbQueue") Queue zfbQueue){
        return BindingBuilder.bind(zfbQueue).to(payExchange).with("pay.zfb").noargs();
    }



}
