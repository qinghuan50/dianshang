package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

import java.util.Map;

/**
 * @Author: wujijun
 * @Description: 订单服务的业务层接口
 * @Date Created in 2022-01-18-21:27
 */
public interface OrderInfoService {


    /**
     * @ClassName orderInfoService
     * @Description 新增订单
     * @Author wujijun
     * @Date 2022/1/18 21:31
     * @Param [orderInfo]
     * @Return void
     * @return
     */
    String addOrder(OrderInfo orderInfo);

    /**
     * @ClassName OrderInfoService
     * @Description 取消订单
     * @Author wujijun
     * @Date 2022/1/20 22:55
     * @Param [orderId, status；主动取消，超时取消]
     * @Return void
     */
    void cancelOrder(Long orderId, String status);

    /**
     * @ClassName OrderInfoServiceImpl
     * @Description 修改订单的支付状态
     * @Author wujijun
     * @Date 2022/1/21 22:53
     * @Param [map, payWay]
     * @Return void
     */
    void updateOrderStatus(Map<String, String> map, String payWay);
}
