package com.atguigu.gmall.cart.mapper;

import com.atguigu.gmall.model.cart.CartInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @Author: wujijun
 * @Description: 购物车表的映射
 * @Date Created in 2022-01-17-20:58
 */
@Mapper
public interface CartInfMapper extends BaseMapper<CartInfo> {

    @Update("update cart_info set is_checked = #{status} where user_id = #{username}")
    Integer updateCheck(@Param("status") Short status, @Param("username") String username);

    @Update("update cart_info set is_checked = #{status} where id = #{id}")
    Integer updateCheckById(@Param("status") Short status, @Param("id") Long id);
}
