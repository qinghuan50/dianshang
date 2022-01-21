package com.atguigu.gmall.pay.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.pay.service.ZfbPayService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: wujijun
 * @Description: 支付宝支付
 * @Date Created in 2022-01-22-0:17
 */
@RestController
@RequestMapping("/zfb/pay")
public class ZfbPayController {

    @Autowired
    private ZfbPayService zfbPayService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * @ClassName ZfbPayController
     * @Description 跳转到支付宝的付款页面
     * @Author wujijun
     * @Date 2022/1/22 0:24
     * @Param [orderId, totalMoney]
     * @Return java.lang.String
     */
    @GetMapping("/getZfbPayUrl")
    public String getZfbPayUrl(String body, String orderId, String money){
        return zfbPayService.getZfbPayUrl(body, orderId, money);
    }

    /**
     * @ClassName ZfbPayController
     * @Description 查询该笔订单的支付状态
     * @Author wujijun
     * @Date 2022/1/22 0:38
     * @Param [orderId]
     * @Return java.lang.String
     */
    @GetMapping("/getZfbPayStatus")
    public String getZfbPayStatus(String orderId){
        return zfbPayService.getZfbPayStatus(orderId);
    }

    /**
     * @ClassName ZfbPayController
     * @Description 同步回调：支付完成以后,支付宝会同步打开的页面
     * @Author wujijun
     * @Date 2022/1/22 0:54
     * @Param [params]
     * @Return java.lang.String
     */
    @GetMapping("/returnPayment")
    public String returnPayment(@RequestParam Map<String, String> map){
        //查询返回的参数
//        System.out.println("同步：" + map);
        return "同步回调成功！";
    }

   /**
    * @ClassName ZfbPayController
    * @Description 异步回调：查询订单支付后是否接收到消息
    * @Author wujijun
    * @Date 2022/1/22 0:54
    * @Param [params]
    * @Return java.lang.String
    */
    @RequestMapping("/notifyPayment")
    public String notifyUrl(@RequestParam Map<String, String> map) {
//        System.out.println("异步：" + map);
        //发消息:通知订单微服务修改订单的支付结果!!!!!!
        rabbitTemplate.convertAndSend("pay_exchange",
                "pay.zfb",
                JSONObject.toJSONString(map));
        //告诉支付宝收到了结果:就不会再发通知了
        return "success";
    }

}
