package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;
import java.util.Map;

/**
 * @Author: wujijun
 * @Description: 购物车的业务接口类
 * @Date Created in 2022-01-17-20:59
 */
public interface CartInfoService {

    /**
     * @return
     * @ClassName CartInfoService
     * @Description 添加购物车
     * @Author wujijun
     * @Date 2022/1/17 21:01
     * @Param [skuId, num]
     * @Return void
     */
    CartInfo addCart(Long skuId, Integer num);

    /**
     * @ClassName CartInfoService
     * @Description 查询购物车中所有的商品信息
     * @Author wujijun
     * @Date 2022/1/17 22:30
     * @Return java.util.List<com.atguigu.gmall.model.cart.CartInfo>
     */
    List<CartInfo> getCart();

    /**
     * @ClassName CartInfoService
     * @Description 删除购物车
     * @Author wujijun
     * @Date 2022/1/17 22:40
     * @Param [id]
     * @Return void
     */
    void delCart(Long id);

    /**
     * @ClassName CartInfoService
     * @Description 改购物车的选中状态
     * @Author wujijun
     * @Date 2022/1/17 22:55
     * @Param [status, id]
     * @Return void
     */
    void updateCheck(Short status, Long id);

    /**
     * @ClassName CartInfoService
     * @Description 购物车的合并（未登录前加入购物车的数据跟登录后的数据合并）
     * @Author wujijun
     * @Date 2022/1/18 0:37
     * @Param []
     * @Return void
     */
    void mergeCart(List<CartInfo> cartInfoList);

    /**
     * @ClassName CartInfoService
     * @Description 获取下单中的购物车信息
     * @Author wujijun
     * @Date 2022/1/18 20:32
     * @Param []
     * @Return java.util.List<com.atguigu.gmall.model.cart.CartInfo>
     */
    Map<String, Object> getCartForOrder();

    /**
     * @ClassName CartInfoService
     * @Description 下单完成后清除订单中购物车中的数据
     * @Author wujijun
     * @Date 2022/1/18 23:28
     * @Param []
     * @Return void
     * @return
     */
    Boolean removeCart();
}
