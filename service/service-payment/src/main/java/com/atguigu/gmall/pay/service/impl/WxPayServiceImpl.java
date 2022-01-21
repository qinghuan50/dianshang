package com.atguigu.gmall.pay.service.impl;

import com.atguigu.gmall.pay.util.HttpClient;
import com.atguigu.gmall.pay.service.WxPayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wujijun
 * @Description: 调用微信支付接口的实现类
 * @Date Created in 2022-01-21-20:41
 */
@Service
public class WxPayServiceImpl implements WxPayService {

    //公众账号ID
    @Value("${weixin.pay.appid}")
    private String appid;

    //商户号
    @Value("${weixin.pay.partner}")
    private String partner;

    //商户key
    @Value("${weixin.pay.partnerkey}")
    private String partnerkey;

    //回调地址
    @Value("${weixin.pay.notifyUrl}")
    private String notifyUrl;

   /**
    * @ClassName WxPayServiceImpl
    * @Description 获取微信支付的二维码
    * @Author wujijun
    * @Date 2022/1/21 21:26
    * @Param [body, orderId, money]
    * @Return java.lang.String
    */
    @Override
    public String getWxPayUrl(String body, String orderId, String money) {
        //获取要调用的微信支付的地址
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        //封装调用接口的参数
        Map<String, String> map = new HashMap<>();
        map.put("appid", appid);
        map.put("mch_id", partner);
        map.put("nonce_str", WXPayUtil.generateNonceStr());
        map.put("body", body);
        map.put("out_trade_no", orderId);
        map.put("total_fee", money);
        map.put("spbill_create_ip", "192.168.200.1");
        map.put("notify_url", notifyUrl);
        map.put("trade_type", "NATIVE");
        try {
            //生成签名，并把map转换成xml
            String mapToXml = WXPayUtil.generateSignedXml(map, partnerkey);
            //想支付接口发送请求
            HttpClient httpClient = new HttpClient(url);
            //设置为https的请求
            httpClient.setHttps(true);
            //设置为xml格式的参数
            httpClient.setXmlParam(mapToXml);
            //发送post的请求
            httpClient.post();
            //微信返回来的结果
            String xmlContent = httpClient.getContent();
            //把微信支付返回来的xml转换成map类型
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(xmlContent);
            //解析结果
            if (xmlToMap.get("return_code").equals("SUCCESS")
                    && xmlToMap.get("result_code").equals("SUCCESS")) {
                //当返回状态码和业务结果都为SUCCESS时，才说明没问题
                //获取二维码的url
                String codeUrl = xmlToMap.get("code_url");
                //返回支付的二维码地址
                return codeUrl;
            }
            //返回结果给客户端
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "参数错误，生成付款二维码失败！";
    }

   /**
    * @ClassName WxPayServiceImpl
    * @Description 查询该笔订单是否支付成功
    * @Author wujijun
    * @Date 2022/1/21 21:28
    * @Param [orderId]
    * @Return java.lang.String
    * @return
    */
    @Override
    public String getWxPayStatus(String orderId) {
        //获取要调用的微信查询的地址
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        //封装调用接口的参数
        Map<String, String> map = new HashMap<>();
        map.put("appid", appid);
        map.put("mch_id", partner);
        map.put("nonce_str", WXPayUtil.generateNonceStr());
        map.put("out_trade_no", orderId);
        try {
            //生成签名，并把map转换成xml
            String mapToXml = WXPayUtil.generateSignedXml(map, partnerkey);
            //想支付接口发送请求
            HttpClient httpClient = new HttpClient(url);
            //设置为https的请求
            httpClient.setHttps(true);
            //设置为xml格式的参数
            httpClient.setXmlParam(mapToXml);
            //发送post的请求
            httpClient.post();
            //微信返回来的结果
            String xmlContent = httpClient.getContent();
            //把微信支付返回来的xml转换成map类型
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(xmlContent);
            //解析结果
            if (xmlToMap.get("return_code").equals("SUCCESS")
                    && xmlToMap.get("result_code").equals("SUCCESS")) {
                //当返回状态码和业务结果都为SUCCESS时，才说明没问题
                String tradeState = xmlToMap.get("trade_state");
                //返回交易的状态
                return tradeState;
            }
            //返回结果给客户端
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "参数错误，获取交易状态失败！";
    }

    /**
     * @param orderId
     * @ClassName WxPayService
     * @Description 关闭该笔交易
     * @Author wujijun
     * @Date 2022/1/21 23:15
     * @Param [orderId]
     * @Return void
     */
    @Override
    public Boolean closePay(String orderId) {
        //获取要调用的微信关闭的地址
        String url = "https://api.mch.weixin.qq.com/pay/closeorder";
        //封装调用接口的参数
        Map<String, String> map = new HashMap<>();
        map.put("appid", appid);
        map.put("mch_id", partner);
        map.put("nonce_str", WXPayUtil.generateNonceStr());
        map.put("out_trade_no", orderId);
        try {
            //生成签名，并把map转换成xml
            String mapToXml = WXPayUtil.generateSignedXml(map, partnerkey);
            //想支付接口发送请求
            HttpClient httpClient = new HttpClient(url);
            //设置为https的请求
            httpClient.setHttps(true);
            //设置为xml格式的参数
            httpClient.setXmlParam(mapToXml);
            //发送post的请求
            httpClient.post();
            //微信返回来的结果
            String xmlContent = httpClient.getContent();
            //把微信支付返回来的xml转换成map类型
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(xmlContent);
            //解析结果
            if (xmlToMap.get("return_code").equals("SUCCESS")
                    && xmlToMap.get("result_code").equals("SUCCESS")) {
                //当返回状态码和业务结果都为SUCCESS时，才说明没问题
                return true;
            }
            //返回结果给客户端
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
