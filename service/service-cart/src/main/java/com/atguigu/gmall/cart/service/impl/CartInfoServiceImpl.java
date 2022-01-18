package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartInfMapper;
import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.cart.util.GmallThreadLocalUtils;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ProductFeign;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: wujijun
 * @Description: 购物车的业务接口实现类
 * @Date Created in 2022-01-17-21:02
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CartInfoServiceImpl implements CartInfoService {

    @Autowired
    private ProductFeign productFeign;

    @Resource
    private CartInfMapper cartInfMapper;

    /**
     * @param skuId
     * @param num
     * @return
     * @ClassName CartInfoService
     * @Description 添加购物车
     * @Author wujijun
     * @Date 2022/1/17 21:01
     * @Param [username, skuId, num]
     * @Return void
     */
    @Override
    public CartInfo addCart(Long skuId, Integer num) {
        //校验参数
        if (skuId == null && num == null) {
            //如果传的参数为空，则手动抛异常，也可使用全局统一异常处理类中已经定义好的异常
            throw new RuntimeException("参数错误！");
        }
        //动态获取用户名（从令牌中）
        String username = GmallThreadLocalUtils.getUserName();
        //判断用户的购物车中是否有该商品
        CartInfo cartInfo = cartInfMapper.selectOne(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, username)
                .eq(CartInfo::getSkuId, skuId));
        //判断查询的结果
        if (cartInfo != null && cartInfo.getId() != null) {
            //有，则合并购物车数据
            num = cartInfo.getSkuNum() + num;
            //判断购物车中的数量是否大于0，小于0直接删除该条数据
            if (num <= 0) {
                //删除购物车
                cartInfMapper.delete(new LambdaQueryWrapper<CartInfo>()
                        .eq(CartInfo::getUserId, username)
                        .eq(CartInfo::getSkuId, skuId));
                return null;
            }
            //合并购物车的商品数量
            cartInfo.setSkuNum(num);
            //购物车中商品的数量大于0
            int i = cartInfMapper.updateById(cartInfo);
            //判断是否合并数据成功
            if (i <= 0) {
                throw new RuntimeException("获取购物车数据失败！");
            }
            return cartInfo;
        }

        //购物车中没有该商品，但是传的值的时候传了一个负数
        if (num <= 0) {
            //直接返回一个空值
            return null;
        }
        //初始化需要返回的对象
        cartInfo = new CartInfo();
        cartInfo.setUserId(username);
        cartInfo.setSkuId(skuId);
        //获取商品的价格
        BigDecimal price = productFeign.getPrice(skuId);
        cartInfo.setCartPrice(price);
        cartInfo.setSkuNum(num);
        //获取商品的信息
        SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
        //判断该商品是否存在
        if (skuInfo == null || skuInfo.getId() == null) {
            //不存在
            throw new RuntimeException("商品不存在！");
        }
        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
        cartInfo.setSkuName(skuInfo.getSkuName());

        //把数据写入数据库
        cartInfMapper.insert(cartInfo);

        //返回结果
        return cartInfo;
    }

    /**
     * @ClassName CartInfoService
     * @Description 查询购物车中所有的商品信息
     * @Author wujijun
     * @Date 2022/1/17 22:30
     * @Param [username]
     * @Return java.util.List<com.atguigu.gmall.model.cart.CartInfo>
     */
    @Override
    public List<CartInfo> getCart() {
        //动态获取用户名（从令牌中）
        String username = GmallThreadLocalUtils.getUserName();

        return cartInfMapper.selectList(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, username));
    }

    /**
     * @param id
     * @ClassName CartInfoService
     * @Description 删除购物车
     * @Author wujijun
     * @Date 2022/1/17 22:40
     * @Param [skuId]
     * @Return void
     */
    @Override
    public void delCart(Long id) {
        cartInfMapper.deleteById(id);
    }

   /**
    * @ClassName CartInfoServiceImpl
    * @Description 修改购物车的选中状态
    * @Author wujijun
    * @Date 2022/1/17 22:55
    * @Param [status, id, username]
    * @Return void
    */
    @Override
    public void updateCheck(Short status, Long id) {
        //动态获取用户名（从令牌中）
        String username = GmallThreadLocalUtils.getUserName();
        //数据库操作后修改的语句数
        int result = 0;
        //判断是全修改还是单个修改
        if (id == null) {
            //id为空，则全修改
            result = cartInfMapper.updateCheck(status, username);
        }
        //修改某一个商品的选中状态
        result = cartInfMapper.updateCheckById(status, id);
        //判断如果数据库修改的条数小于0，则修改失败
        if (result < 0) {
            throw new RuntimeException("状态修改错误！");
        }
    }

    /**
     * @param cartInfoList
     * @ClassName CartInfoService
     * @Description 购物车的合并（未登录前加入购物车的数据跟登录后的数据合并）
     * @Author wujijun
     * @Date 2022/1/18 0:37
     * @Param []
     * @Return void
     */
    @Override
    public void mergeCart(List<CartInfo> cartInfoList) {
        //把未登录前购物车的数据一件一件的加入登录后的购物车中
        cartInfoList.stream().forEach(cartInfo -> {
            //新增购物车数据
            this.addCart(cartInfo.getSkuId(), cartInfo.getSkuNum());
        });
    }
}
