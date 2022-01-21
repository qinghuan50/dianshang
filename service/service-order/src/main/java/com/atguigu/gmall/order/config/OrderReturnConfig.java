package com.atguigu.gmall.order.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: wujijun
 * @Description: 自定义的return模式
 * @Date Created in 2022-01-20-20:45
 */
@Component
@Log4j2
public class OrderReturnConfig implements RabbitTemplate.ReturnCallback {

    /**
     * @ClassName ProductReturnConfig
     * @Description 消息可靠性的投递
     * @Author wujijun
     * @Date 2022/1/20 20:48
     * @Param [message 消息, i 状态码, s 错误的内容, s1 交换机, s2 路由的key]
     * @Return void
     */
    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        log.error("出错的消息：" + message);
        log.error("出错的状态码：" + i);
        log.error("出错的内容：" + s);
        log.error("出错的交换机：" + s1);
        log.error("出错的路由：" + s2);
    }
}
