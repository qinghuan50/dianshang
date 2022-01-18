package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.common.constant.CartConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: wujijun
 * @Description: 购物车服务的表述层
 * @Date Created in 2022-01-17-21:23
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartInfoService cartInfoService;

    /**
     * @ClassName CartController
     * @Description 往购物车中新增商品
     * @Author wujijun
     * @Date 2022/1/17 23:31
     * @Param [skuId, num]
     * @Return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/addCart")
    public Result addCart(Long skuId, Integer num){
        //从令牌中获取用户名
        String username = "wujijun";
        return Result.ok(cartInfoService.addCart(skuId, num));
    }

    /**
     * @ClassName CartController
     * @Description 查询购物车中所有商品的信息
     * @Author wujijun
     * @Date 2022/1/17 22:36
     * @Param []
     * @Return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/getCart")
    public Result getCart(){
        //获取当前登录的用户名
//        String username = GmallThreadLocalUtils.getUserName();
//        String username = "wujijun";
        return Result.ok(cartInfoService.getCart());
    }

    /**
     * @ClassName CartController
     * @Description 删除购物车中某一种商品
     * @Author wujijun
     * @Date 2022/1/17 22:43
     * @Param [id]
     * @Return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/delCart")
    public Result delCart(Long id){
        cartInfoService.delCart(id);
        return Result.ok();
    }

    /**
     * @ClassName CartController
     * @Description 商品选中的状态
     * @Author wujijun
     * @Date 2022/1/17 23:18
     * @Param [id]
     * @Return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/onCheck")
    public Result onCheck(Long id){
        //获取登录的用户名
        String username = "wujijun";
        //选中的状态
        cartInfoService.updateCheck(CartConst.CART_STATUS_ONCHECKED, id);
        return Result.ok();
    }

    /**
     * @ClassName CartController
     * @Description 商品未选中的状态
     * @Author wujijun
     * @Date 2022/1/17 23:22
     * @Param [id]
     * @Return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/canceCheck")
    public Result canceCheck(Long id){
        //获取登录的用户名
        String username = "wujijun";
        //未选中的状态
        cartInfoService.updateCheck(CartConst.CART_STATUS_CANCECHECKED, id);
        return Result.ok();
    }

    /**
     * @ClassName CartController
     * @Description 合并购物车
     * @Author wujijun
     * @Date 2022/1/18 0:42
     * @Param [cartInfoList]
     * @Return com.atguigu.gmall.common.result.Result
     */
    @PostMapping("/mergeCart")
    public Result mergeCart(@RequestBody List<CartInfo> cartInfoList){
        cartInfoService.mergeCart(cartInfoList);
        return Result.ok();
    }
}
