package com.atguigu.gmall.user.mapper;

import com.atguigu.gmall.model.user.UserAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: wujijun
 * @Description: 用户收获地址的映射
 * @Date Created in 2022-01-16-23:44
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {
}
