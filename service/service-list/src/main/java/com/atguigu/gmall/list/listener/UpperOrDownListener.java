package com.atguigu.gmall.list.listener;

import com.atguigu.gmall.list.service.ListService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @Author: wujijun
 * @Description: 商品上下架的消费者
 * @Date Created in 2022-01-20-21:12
 */
@Component
@Log4j2
public class UpperOrDownListener {

    @Autowired
    private ListService listService;

    /**
     * @ClassName UpperOrDownListener
     * @Description 商品的上架
     * @Author wujijun
     * @Date 2022/1/20 21:39
     * @Param [channel, message]
     * @Return void
     */
    @RabbitListener(queues = "sku_upper_queue")
    public void skuUp(Channel channel, Message message) {
        //获取消息；字节数组
        byte[] body = message.getBody();
        //把字节数组转换成字符类型,在强转成我们需要的类型
        long skuId = Long.parseLong(new String(body));
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long tag = messageProperties.getDeliveryTag();
        try {
            //消费消息
            listService.addSkuEs(skuId);
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
                    log.error("消费消息失败，商品上架失败，商品的id为：" + skuId);
                }
            } catch (Exception ex) {
                log.error("拒绝消息失败，商品上架失败，商品的id为：" + skuId);
            }
        }
    }

    /**
     * @ClassName UpperOrDownListener
     * @Description 商品下架
     * @Author wujijun
     * @Date 2022/1/20 21:44
     * @Param [channel, message]
     * @Return void
     */
    @RabbitListener(queues = "sku_down_queue")
    public void skuDown(Channel channel, Message message) {
        //获取消息；字节数组
        byte[] body = message.getBody();
        //把字节数组转换成字符类型,在强转成我们需要的类型
        long skuId = Long.parseLong(new String(body));
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long tag = messageProperties.getDeliveryTag();
        try {
            //消费消息
            listService.delSkuEs(skuId);
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
                    log.error("消费消息失败，商品下架失败，商品的id为：" + skuId);
                }
            } catch (Exception ex) {
                log.error("拒绝消息失败，商品下架失败，商品的id为：" + skuId);
            }
        }
    }


}
