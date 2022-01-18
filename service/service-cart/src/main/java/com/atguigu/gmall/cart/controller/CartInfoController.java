package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: wujijun
 * @Description:  服务之间内部的调用
 * @Date Created in 2022-01-18-21:42
 */
@RestController
@RequestMapping("/cart")
public class CartInfoController {

    @Autowired
    private CartInfoService cartInfoService;

    /**
     * @ClassName CartController
     * @Description 获取下单后购物车中的商品
     * @Author wujijun
     * @Date 2022/1/18 20:41
     * @Param []
     * @Return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/getCartForOrder")
    public Map<String, Object> getCartForOrder(){
        return cartInfoService.getCartForOrder();
    }

    /*
     * @ClassName CartInfoController
     * @Description 清除订单中的购物车的商品
     * @Author wujijun
     * @Date 2022/1/18 23:36
     * @Param
     * @Return
     */
    @GetMapping("/removeCart")
    public Boolean removeCart(){
        return cartInfoService.removeCart();
    }
}

