package com.atguigu.gmall.oauth.controller;

import brave.http.HttpServerRequest;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: wujijun
 * @Description: 登录的自定义接口
 * @Date Created in 2022-01-16-22:10
 */
@RestController
@RequestMapping("/user/login")
public class LoginController {

    @Autowired
    LoginService loginService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @ClassName LoginController
     * @Description 登录验证
     * @Author wujijun
     * @Date 2022/1/16 22:11
     * @Param []
     * @Return com.atguigu.gmall.common.result.Result
     */
    @PostMapping
    public Result login(String username, String password){
        //获取登录令牌
        AuthToken authToken = loginService.login(username, password);
        //从登录信息中获取ip信息
        String ipAddress = IpUtil.getIpAddress(request);
        //将ip地址和令牌存入缓存中
        stringRedisTemplate.opsForValue().set(ipAddress, authToken.getAccessToken());
        //返回结果
        return Result.ok(authToken);
    }
}
