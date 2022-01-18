package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: wujijun
 * @Description: 用户收获地址的表述层
 * @Date Created in 2022-01-16-23:51
 */
@RestController
@RequestMapping("/api/user/address")
public class UserAddressController {

    @Autowired
    UserAddressService userAddressService;

    /**
     * @ClassName UserAddressController
     * @Description 获取用户的所有的收获地址
     * @Author wujijun
     * @Date 2022/1/16 23:58
     * @Param []
     * @Return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/getUserAddress")
    public Result getUserAddress(){
        String username = "qh";
        return Result.ok(userAddressService.getUserAddress(username));
    }

}
