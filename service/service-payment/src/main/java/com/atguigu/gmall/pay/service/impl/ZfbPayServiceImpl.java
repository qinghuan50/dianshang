package com.atguigu.gmall.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall.pay.service.ZfbPayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Author: wujijun
 * @Description: 支付宝的支付接口的实现类
 * @Date Created in 2022-01-22-0:03
 */
@Service
public class ZfbPayServiceImpl implements ZfbPayService {

    @Value("${alipay_url}")
    private String alipayUrl;

    @Value("${app_id}")
    private String appId;

    @Value("${app_private_key}")
    private String appPrivateKey;

    @Value("${alipay_public_key}")
    private String alipayPublicKey;

    @Value("${return_payment_url}")
    private String returnPaymentUrl;

    @Value("${notify_payment_url}")
    private String notifyPaymentUrl;


    /**
     * @ClassName ZfbPayService
     * @Description 跳转到支付宝付款的页面
     * @Author wujijun
     * @Date 2022/1/22 0:29
     * @Param [orderId, totalMoney]
     * @Return java.lang.String
     */
    @Override
    public String getZfbPayUrl(String body, String orderId, String money) {
        //初始化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(alipayUrl,
                appId,
                appPrivateKey,
                "json",
                "UTF-8",
                alipayPublicKey,
                "RSA2");
        //初始化request
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //设置异步通知的地址
        request.setNotifyUrl(notifyPaymentUrl);
        //设置同步回调的地址
        request.setReturnUrl(returnPaymentUrl);
        //封装请求的参数
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", money);
        bizContent.put("subject", body);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if (response.isSuccess()) {
                //返回一个页面
                return response.getBody();
            } else {
                throw new RuntimeException("参数错误，调用失败请重试！");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new RuntimeException("参数错误，调用失败请重试！");
        }
    }

    /**
     * @param orderId
     * @ClassName ZfbPayService
     * @Description 查询该笔订单的交易状态
     * @Author wujijun
     * @Date 2022/1/22 0:30
     * @Param [orderId]
     * @Return java.lang.String
     */
    @Override
    public String getZfbPayStatus(String orderId) {
        //初始化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(alipayUrl,
                appId,
                appPrivateKey,
                "json",
                "UTF-8",
                alipayPublicKey,
                "RSA2");
        //初始化request
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        request.setBizContent(bizContent.toString());
        try {
            //发送请求
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                //返回的是json类型的字符串
               return response.getBody();
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}
