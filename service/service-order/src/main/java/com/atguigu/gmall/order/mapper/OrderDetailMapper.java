package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: wujijun
 * @Description: 订单详情表的mapeer映射
 * @Date Created in 2022-01-18-21:26
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
