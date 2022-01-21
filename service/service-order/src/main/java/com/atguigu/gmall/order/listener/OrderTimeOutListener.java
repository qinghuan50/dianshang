package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: wujijun
 * @Description: 消费者（消费消息），超时未支付的
 * @Date Created in 2022-01-20-22:44
 */
@Component
@Log4j2
public class OrderTimeOutListener {

    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * @ClassName OrderTimeOutListener
     * @Description 延迟消息，取消超时订单
     * @Author wujijun
     * @Date 2022/1/20 22:49
     * @Param [channel, message]
     * @Return void
     */
    @RabbitListener(queues = "die_queue")
    public void orderTimeOut(Channel channel, Message message){
        //获取消息；字节数组
        byte[] body = message.getBody();
        //把字节数组转换成字符类型,在强转成我们需要的类型
        long orderId = Long.parseLong(new String(body));
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long tag = messageProperties.getDeliveryTag();
        try {
            //消费消息
            orderInfoService.cancelOrder(orderId, OrderStatus.TIMEOUT_CANCELLATION.getComment());
            //确认消息
            channel.basicAck(tag, false);
        } catch (Exception e) {
            try {
                //判断消息是否被消费过
                if (!messageProperties.getRedelivered()) {
                    //再消费一次，不把消息放回消息队列
                    channel.basicReject(tag, true);
                } else {
                    //如果已经消费过，则直接拒拒绝消费，把数据存入日志中
                    channel.basicReject(tag, false);
                    log.error("消费消息失败，订单超时取消，订单号的id为：" + orderId);
                }
            } catch (Exception ex) {
                log.error("拒绝消息失败，订单超时取消，订单的id为：" + orderId);
            }
        }
    }

}
