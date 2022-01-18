package com.atguigu.gmall.oauth.service;

import com.atguigu.gmall.oauth.util.AuthToken;

/**
 * @Author: wujijun
 * @Description: 用户登录的接口
 * @Date Created in 2022-01-16-22:13
 */
public interface LoginService {

    /**
     * @ClassName LoginService
     * @Description 用户登录
     * @Author wujijun
     * @Date 2022/1/16 22:13
     * @Param [username, password]
     * @Return void
     * @return
     */
    AuthToken login(String username, String password);
}
