package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: wujijun
 * @Description: 新增订单的
 * @Date Created in 2022-01-18-22:23
 */
@RestController
@RequestMapping("/api/order")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * @ClassName OrderInfoController
     * @Description 新增订单
     * @Author wujijun
     * @Date 2022/1/18 22:27
     * @Param [orderInfo]
     * @Return com.atguigu.gmall.common.result.Result
     */
    @PostMapping("/addOrder")
    public Result addOrder(@RequestBody OrderInfo orderInfo){
        return Result.ok(orderInfoService.addOrder(orderInfo));
    }

    /**
     * @ClassName OrderInfoController
     * @Description 用户主动取消订单
     * @Author wujijun
     * @Date 2022/1/20 23:12
     * @Param [order, status]
     * @Return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/cancelOrder")
    public Result cancelOrder(Long orderId){
        orderInfoService.cancelOrder(orderId, OrderStatus.ACTIVE_CANCELLATION.getComment());
        return Result.ok();
    }
}
