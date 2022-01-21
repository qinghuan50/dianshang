package com.atguigu.gmall.pay.fegn;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: wujijun
 * @Description:  支付微服务的feign接口
 * @Date Created in 2022-01-21-23:33
 */
@FeignClient(name = "service-payment")
public interface PayFeign {

    /**
     * @ClassName WxPayController
     * @Description 获取微信支付的二维码
     * @Author wujijun
     * @Date 2022/1/21 21:18
     * @Param [body, orderId, money]
     * @Return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/wx/pay/getWxPayUrl")
    public String getWxPayUrl(String body, String orderId, String money);

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
    @GetMapping("/wx/pay/getWxPayStatus")
    public String getWxPayStatus(String orderId);

    /**
     * @ClassName WxPayController
     * @Description 关闭该笔订单
     * @Author wujijun
     * @Date 2022/1/21 23:31
     * @Param [orderId]
     * @Return java.lang.Boolean
     */
    @GetMapping("/wx/pay/closePay/{orderId}")
    public Boolean closePay(@PathVariable("orderId") String orderId);

}
