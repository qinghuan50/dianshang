package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

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
    OrderInfo addOrder(OrderInfo orderInfo);
}
