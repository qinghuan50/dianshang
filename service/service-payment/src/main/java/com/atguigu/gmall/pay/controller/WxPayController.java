package com.atguigu.gmall.pay.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.pay.service.WxPayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wujijun
 * @Description: 微信支付返回给前端的内容
 * @Date Created in 2022-01-21-21:14
 */
@RestController
@RequestMapping("/wx/pay")
public class WxPayController {

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * @ClassName WxPayController
     * @Description 获取微信支付的二维码
     * @Author wujijun
     * @Date 2022/1/21 21:18
     * @Param [body, orderId, money]
     * @Return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/getWxPayUrl")
    public String getWxPayUrl(String body, String orderId, String money) {
        return wxPayService.getWxPayUrl(body, orderId, money);
    }

    /**
     * @ClassName WxPayController
     * @Description 获取该笔订单的交易状态；
     * SUCCESS--支付成功
     * REFUND--转入退款
     * NOTPAY--未支付
     * CLOSED--已关闭
     * REVOKED--已撤销(刷卡支付)
     * USERPAYING--用户支付中
     * PAYERROR--支付失败(其他原因，如银行返回失败)
     * ACCEPT--已接收，等待扣款
     * @Author wujijun
     * @Date 2022/1/21 21:33
     * @Param [orderId]
     * @Return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/getWxPayStatus")
    public String getWxPayStatus(String orderId) {
        return wxPayService.getWxPayStatus(orderId);
    }

    /**
     * @ClassName WxPayController
     * @Description 微信支付调用异步通知的支付结果
     * @Author wujijun
     * @Date 2022/1/21 21:46
     * @Param [request]
     * @Return java.lang.String
     */
    @RequestMapping("/notify")
    public String notify(HttpServletRequest request) {
        try {
            //获取支付结果的数据流
            ServletInputStream inputStream = request.getInputStream();
            //创建一个输出流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            //设置每次读取的量
            byte[] bytes = new byte[1024];
            //初始化长度
            int len = 0;
            //数据流中读取数据
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
            //获取输出流的数组
            byte[] byteArray = outputStream.toByteArray();
            //把数组转换成字符串
            String xmlString = new String(byteArray, "UTF-8");
            //将xml格式转换成map
            Map<String, String> map = WXPayUtil.xmlToMap(xmlString);
            //打印一下，看下腾讯给我们返回了那些数据
//            System.out.println("map = " + map);
            //支付成功后，调用订单，修改订单状态；方案一：通过feign调用；方案二：通过消息队列（推荐）
            rabbitTemplate.convertAndSend("pay_exchange",
                                        "pay.wx",
                                        JSONObject.toJSONString(map));
            //响应微信支付的请求
            HashMap<String, String> resultMap = new HashMap<>();
            //封装返回的参数
            resultMap.put("return_code", "SUCCESS");
            resultMap.put("return_msg", "OK");
            //把map转换成xml格式返回给微信支付
            return WXPayUtil.mapToXml(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "参数错误，调用异步通知出错！";
    }

    /**
     * @ClassName WxPayController
     * @Description 关闭该笔订单
     * @Author wujijun
     * @Date 2022/1/21 23:31
     * @Param [orderId]
     * @Return java.lang.Boolean
     */
    @GetMapping("/closePay/{orderId}")
    public Boolean closePay(@PathVariable("orderId") String orderId){
        return wxPayService.closePay(orderId);
    }

}
