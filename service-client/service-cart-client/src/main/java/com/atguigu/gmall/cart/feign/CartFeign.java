package com.atguigu.gmall.cart.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * @Author: wujijun
 * @Description: 购物车微服务的feign接口
 * @Date Created in 2022-01-18-21:50
 */
@FeignClient(name = "service-cart", path = "/cart")
public interface CartFeign {

    /**
     * @ClassName CartFeign
     * @Description 购物车商品列表的查询
     * @Author wujijun
     * @Date 2022/1/18 21:51
     * @Param []
     * @Return java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping("/getCartForOrder")
    Map<String, Object> getCartForOrder();

    /*
     * @ClassName CartInfoController
     * @Description 清除订单中的购物车的商品
     * @Author wujijun
     * @Date 2022/1/18 23:36
     * @Param
     * @Return
     */
    @GetMapping("/removeCart")
    Boolean removeCart();
}
