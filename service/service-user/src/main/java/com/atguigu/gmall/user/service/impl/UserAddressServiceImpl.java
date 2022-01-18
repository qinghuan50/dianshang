package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.service.UserAddressService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: wujijun
 * @Description: 用户收获地址的实现类
 * @Date Created in 2022-01-17-0:04
 */
@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Resource
    UserAddressMapper userAddressMapper;

    /**
     * @param username
     * @ClassName UserService
     * @Description 通过用户名查询用户的收获地址
     * @Author wujijun
     * @Date 2022/1/16 23:46
     * @Param [username]
     * @Return java.util.List<com.atguigu.gmall.model.user.UserAddress>
     */
    @Override
    public List<UserAddress> getUserAddress(String username) {
        return userAddressMapper.selectList(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId,username));
    }
}
