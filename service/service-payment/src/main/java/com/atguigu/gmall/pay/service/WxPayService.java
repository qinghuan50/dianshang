package com.atguigu.gmall.pay.service;

/**
 * @Author: wujijun
 * @Description: 调用微信支付的接口
 * @Date Created in 2022-01-21-20:39
 */
public interface WxPayService {

    /**
     * @ClassName WxPayService
     * @Description 获取微信支付的二维码
     * @Author wujijun
     * @Date 2022/1/21 20:40
     * @Param []
     * @Return void
     * @return
     */
    String getWxPayUrl(String body, String orderId, String money);

    /**
     * @ClassName WxPayService
     * @Description 查询该笔订单是否支付成功
     * @Author wujijun
     * @Date 2022/1/21 21:26
     * @Param []
     * @Return void
     * @return
     */
    String getWxPayStatus(String orderId);


    /**
     * @ClassName WxPayService
     * @Description 关闭该笔交易
     * @Author wujijun
     * @Date 2022/1/21 23:15
     * @Param [orderId]
     * @Return void
     */
    Boolean closePay(String orderId);
}
