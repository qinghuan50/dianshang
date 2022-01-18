package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;

import java.util.List;

/**
 * @Author: wujijun
 * @Description: 用户收获地址的接口
 * @Date Created in 2022-01-16-23:45
 */
public interface UserAddressService {

    /**
     * @ClassName UserService
     * @Description 通过用户名查询用户的收获地址
     * @Author wujijun
     * @Date 2022/1/16 23:46
     * @Param [username]
     * @Return java.util.List<com.atguigu.gmall.model.user.UserAddress>
     */
    List<UserAddress> getUserAddress(String username);
}