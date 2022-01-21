package com.atguigu.gmall.product.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: wujijun
 * @Description: 配置交换机和队列
 * @Date Created in 2022-01-20-20:57
 */
@Configuration
public class ProductRabbitConfig {

    /**
     * @ClassName ProductRabbitConfig
     * @Description 创建交换机
     * @Author wujijun
     * @Date 2022/1/20 20:59
     * @Param []
     * @Return org.springframework.amqp.core.Exchange
     */
    @Bean("skuExchange")
    public Exchange skuExchange(){
        return ExchangeBuilder.directExchange("sku_up_down_exchange").build();
    }

    /**
     * @ClassName ProductRabbitConfig
     * @Description 商品上架的队列
     * @Author wujijun
     * @Date 2022/1/20 21:02
     * @Param []
     * @Return org.springframework.amqp.core.Queue
     */
    @Bean("skuUpperQueue")
    public Queue skuUpperQueue(){
        return QueueBuilder.durable("sku_upper_queue").build();
    }

    /**
     * @ClassName ProductRabbitConfig
     * @Description 交换机与上架的队列绑定
     * @Author wujijun
     * @Date 2022/1/20 21:06
     * @Param [skuUpperQueue, skuExchange]
     * @Return org.springframework.amqp.core.Binding
     */
    @Bean
    public Binding upperBinding(@Qualifier("skuUpperQueue") Queue skuUpperQueue,
                                @Qualifier("skuExchange")Exchange skuExchange){
        return BindingBuilder.bind(skuUpperQueue).to(skuExchange).with("sku.upper").noargs();
    }

    /**
     * @ClassName ProductRabbitConfig
     * @Description 商品下架的队列
     * @Author wujijun
     * @Date 2022/1/20 21:02
     * @Param []
     * @Return org.springframework.amqp.core.Queue
     */
    @Bean("skuDownQueue")
    public Queue skuDownQueue(){
        return QueueBuilder.durable("sku_down_queue").build();
    }

    /**
     * @ClassName ProductRabbitConfig
     * @Description 交换机与下架的队列绑定
     * @Author wujijun
     * @Date 2022/1/20 21:06
     * @Param [skuUpperQueue, skuExchange]
     * @Return org.springframework.amqp.core.Binding
     */
    @Bean
    public Binding downBinding(@Qualifier("skuDownQueue") Queue skuDownQueue,
                                @Qualifier("skuExchange")Exchange skuExchange){
        return BindingBuilder.bind(skuDownQueue).to(skuExchange).with("sku.down").noargs();
    }
}
