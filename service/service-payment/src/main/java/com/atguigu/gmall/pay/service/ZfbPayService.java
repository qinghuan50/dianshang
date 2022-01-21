package com.atguigu.gmall.pay.service;

/**
 * @Author: wujijun
 * @Description: 支付宝的支付接口
 * @Date Created in 2022-01-22-0:01
 */
public interface ZfbPayService {

    /**
     * @ClassName ZfbPayService
     * @Description 跳转到支付宝付款的页面
     * @Author wujijun
     * @Date 2022/1/22 0:29
     * @Param [orderId, totalMoney]
     * @Return java.lang.String
     */
    String getZfbPayUrl(String body, String orderId, String money);

    /**
     * @ClassName ZfbPayService
     * @Description 查询该笔订单的交易状态
     * @Author wujijun
     * @Date 2022/1/22 0:30
     * @Param [orderId]
     * @Return java.lang.String
     */
    String getZfbPayStatus(String orderId);
}
