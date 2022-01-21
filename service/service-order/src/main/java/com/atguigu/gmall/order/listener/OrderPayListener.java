package com.atguigu.gmall.order.listener;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.enums.PaymentType;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: wujijun
 * @Description: 支付结果的消费消息
 * @Date Created in 2022-01-21-22:40
 */
@Component
@Log4j2
public class OrderPayListener {

    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * @ClassName OrderPayListener
     * @Description 监听微信支付结果
     * @Author wujijun
     * @Date 2022/1/21 22:43
     * @Param [channel, message]
     * @Return void
     */
    @RabbitListener(queues = "wx_pay_queue")
    public void orderWxPay(Channel channel, Message message){
        //获取消息；字节数组
        byte[] body = message.getBody();
        //把字节数组转换成字符类型
        String s = new String(body);
        //反序列化，因为我们传过来的时候做了序列化
        Map<String, String> map = JSONObject.parseObject(s, Map.class);
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long tag = messageProperties.getDeliveryTag();
        try {
            //消费消息
            orderInfoService.updateOrderStatus(map, PaymentType.WEIXIN.getComment());
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
                    log.error("消费消息失败，修改订单支付结果，详情：" + s);
                }
            } catch (Exception ex) {
                log.error("拒绝消息失败，修改订单支付结果，详情：" + s);
            }
        }
    }

    /**
     * @ClassName OrderPayListener
     * @Description 监听支付宝支付结果
     * @Author wujijun
     * @Date 2022/1/21 22:43
     * @Param [channel, message]
     * @Return void
     */
    @RabbitListener(queues = "zfb_pay_queue")
    public void orderZfbPay(Channel channel, Message message){
        //获取消息；字节数组
        byte[] body = message.getBody();
        //把字节数组转换成字符类型
        String s = new String(body);
        //反序列化，因为我们传过来的时候做了序列化
        Map<String, String> map = JSONObject.parseObject(s, Map.class);
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long tag = messageProperties.getDeliveryTag();
        try {
            //消费消息
            orderInfoService.updateOrderStatus(map, PaymentType.ALIPAY.getComment());
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
                    log.error("消费消息失败，修改订单支付结果，详情：" + s);
                }
            } catch (Exception ex) {
                log.error("拒绝消息失败，修改订单支付结果，详情：" + s);
            }
        }
    }
}
